package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType

val expectationType = PhpType().apply {
    this.add("\\Pest\\Expectations\\Expectation")
}

fun PhpType.isExpectation(project: Project): Boolean {
    val filteredType = this.filterMixed()

    if (filteredType.isEmpty) {
        return false
    }

    return expectationType.isConvertibleFrom(
        filteredType,
        PhpIndex.getInstance(project)
    )
}

fun PsiElement.isPestExtendReference(): Boolean {
    if (this !is MethodReferenceImpl) {
        return false
    }

    if (this.canonicalText != "extend") {
        return false
    }

    val expectReference = this.firstChild

    if (expectReference !is FunctionReferenceImpl) {
        return false
    }

    if (expectReference.canonicalText != "expect") {
        return false
    }

    return true
}

val MethodReferenceImpl.extendName: String?
    get() {
        val name = this.getParameter(0) ?: return null

        if (name !is StringLiteralExpression) {
            return null
        }

        return name.contents
    }

val PsiFile.expectExtends: List<MethodReferenceImpl>
    get() {
        if (this !is PhpFile) return emptyList()

        val element = this.firstChild

        return element.children.filterIsInstance<PhpNamespace>()
            .mapNotNull { it.statements }
            .getOrElse(
                0
            ) { element }
            .children
            .filterIsInstance<Statement>()
            .mapNotNull { it.firstChild }
            .filterIsInstance<MethodReferenceImpl>()
            .filter { it.isPestExtendReference() }
    }

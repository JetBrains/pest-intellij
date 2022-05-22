package com.pestphp.pest.customExpectations

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpExpression
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.customExpectations.generators.Method
import com.pestphp.pest.customExpectations.generators.Parameter

val expectationType = PhpType().apply {
    this.add("\\Pest\\Expectation")
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

fun Statement.isExpectation(): Boolean {
    return (this.firstPsiChild as? MethodReference)?.isExpectation() == true
}

fun MethodReference.isExpectation(): Boolean {
    return this.type.isExpectation(this.project)
}

fun PsiElement?.isThisVariableInExtend(): Boolean {
    if ((this as? Variable)?.name != "this") return false

    val closure = PsiTreeUtil.getParentOfType(this, Function::class.java)

    if (closure == null || !closure.isClosure) return false

    val parameterList = closure.parent?.parent as? ParameterList ?: return false

    return parameterList.parent.isPestExtendReference()
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

val PsiFile.customExpects: List<MethodReferenceImpl>
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

fun MethodReferenceImpl.toMethod(): Method? {
    val extendName = this.extendName ?: return null

    val closure = (this.parameters[1] as? PhpExpression)?.firstChild as? Function

    if (closure === null) {
        return null
    }

    return Method(
        extendName,
        closure.type,
        closure.parameters.map { parameter ->
            Parameter(
                parameter.name,
                parameter.type,
                parameter.defaultValuePresentation
            )
        }
    )
}
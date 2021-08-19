package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

fun PsiElement?.isPestTestReference(): Boolean {
    return when (this) {
        null -> false
        is MethodReference -> this.isPestTestMethodReference()
        is FunctionReferenceImpl -> this.isPestTestFunction()
        else -> false
    }
}

private val testNames = setOf("it", "test")
fun FunctionReferenceImpl.isPestTestFunction(): Boolean {
    return this.canonicalText in testNames
}

fun FunctionReferenceImpl.isPestBeforeFunction(): Boolean {
    return this.canonicalText == "beforeEach"
}

fun FunctionReferenceImpl.isPestAfterFunction(): Boolean {
    return this.canonicalText == "afterEach"
}

private val allPestNames = setOf("it", "test", "beforeEach", "afterEach")
fun FunctionReferenceImpl.isAnyPestFunction(): Boolean {
    return this.canonicalText in allPestNames
}

fun MethodReference.isPestTestMethodReference(): Boolean {
    return when (val reference = classReference) {
        is FunctionReferenceImpl -> reference.isPestTestFunction()
        is MethodReference -> reference.isPestTestMethodReference()
        else -> false
    }
}

fun PsiFile.getRoot(): List<PsiElement> {
    val element = this.firstChild

    return element.children.filterIsInstance<PhpNamespace>()
        .mapNotNull { it.statements }
        .getOrElse(
            0
        ) { element }
        .children
        .filterIsInstance<Statement>()
        .mapNotNull { it.firstChild }
}

fun PsiFile.getPestTests(): Set<FunctionReference> {
    if (this !is PhpFile) return setOf()

    return this.getRoot()
        .filter(PsiElement::isPestTestReference)
        .filterIsInstance<FunctionReference>()
        .toSet()
}

package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
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

private val beforeNames = setOf("beforeEach", "beforeAll")
fun FunctionReferenceImpl.isPestBeforeFunction(): Boolean {
    return this.canonicalText in beforeNames
}

private val afterNames = setOf("afterEach", "afterAll")
fun FunctionReferenceImpl.isPestAfterFunction(): Boolean {
    return this.canonicalText in afterNames
}

private val allPestNames = setOf("it", "test", "beforeEach", "beforeAll", "afterAll", "afterEach")
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

fun FunctionReferenceImpl.getPestTestName(): String? {
    return (getParameter(0) as? StringLiteralExpression)?.contents
}

fun PsiElement?.getPestTestName(): String? {
    return when (this) {
        is MethodReference -> (this.classReference as? FunctionReference)?.getPestTestName()
        is FunctionReferenceImpl -> this.getPestTestName()
        else -> null
    }
}

fun PsiFile.getPestTests(): Set<FunctionReference> {
    return PsiTreeUtil.findChildrenOfType(this, FunctionReference::class.java).toSet()
}

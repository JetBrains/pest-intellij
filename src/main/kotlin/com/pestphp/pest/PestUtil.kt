package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager

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

fun PsiFile.isPestTestFile(): Boolean {
    if (this !is PhpFile) return false

    return PsiTreeUtil.findChildrenOfType(this, FunctionReferenceImpl::class.java)
        .any(FunctionReferenceImpl::isPestTestFunction)
}

fun PsiFile.isPestConfigurationFile(): Boolean {
    return PhpUnitUtil.isPhpUnitConfigurationFile(this)
}

fun Project.isPestEnabled(): Boolean {
    return PhpTestFrameworkSettingsManager
        .getInstance(this)
        .getConfigurations(PestFrameworkType.getInstance())
        .any { StringUtil.isNotEmpty(it.executablePath) }
}

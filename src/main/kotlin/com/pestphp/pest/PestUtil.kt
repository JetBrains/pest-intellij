package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ObjectUtils
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager

fun PsiElement?.isPestTestFunction(): Boolean {
    return when (this) {
        null -> false
        is MethodReference -> this.isPestTestFunction()
        is FunctionReference -> this.isPestTestFunction()
        else -> false
    }
}

fun FunctionReference.isPestTestFunction(): Boolean {
    return this.canonicalText in setOf("it", "test")
}

fun MethodReference.isPestTestFunction(): Boolean {
    val reference = ObjectUtils.tryCast(this.classReference, FunctionReference::class.java)
    return reference != null && reference.isPestTestFunction()
}

fun FunctionReference.getPestTestName(): String? {
    return when (val parameter = this.getParameter(0)) {
        is StringLiteralExpression -> parameter.contents
        else -> null
    }
}

fun PsiElement?.getPestTestName(): String? {
    return when (this) {
        is MethodReference -> (this.classReference as? FunctionReference)?.getPestTestName()
        is FunctionReference -> this.getPestTestName()
        else -> null
    }
}

fun PsiFile.isPestTestFile(): Boolean {
    return when (this) {
        is PhpFile -> {
            PsiTreeUtil.findChildrenOfType(this, FunctionReference::class.java)
                .asSequence()
                .any(FunctionReference::isPestTestFunction)
        }
        else -> false
    }
}

fun PsiFile.isPestConfigurationFile(): Boolean {
    return PhpUnitUtil.isPhpUnitConfigurationFile(this)
}

fun Project.isPestEnabled(): Boolean {
    return PhpTestFrameworkSettingsManager
        .getInstance(this)
        .getConfigurations(PestFrameworkType.getInstance())
        .stream()
        .anyMatch { config: PhpTestFrameworkConfiguration -> StringUtil.isNotEmpty(config.executablePath) }
}

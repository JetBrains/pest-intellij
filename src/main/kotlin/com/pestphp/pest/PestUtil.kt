package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager

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
        .getConfigurations(PestFrameworkType.instance)
        .any { StringUtil.isNotEmpty(it.executablePath) }
}

package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import java.lang.Exception

@Suppress("TooGenericExceptionCaught")
fun PsiFile.isPestTestFile(): Boolean {
    if (this !is PhpFile) return false

    return try {
        val element = this.firstChild

        element.children.filterIsInstance<PhpNamespace>()
            .mapNotNull { it.statements }
            .getOrElse(
                0
            ) { element }
            .children
            .filterIsInstance<Statement>()
            .mapNotNull { it.firstChild }
            .any(PsiElement::isPestTestReference)
    } catch (e: Exception) {
        false
    }
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

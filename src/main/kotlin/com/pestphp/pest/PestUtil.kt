package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager

@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun PsiFile.isPestTestFile(): Boolean {
    if (this !is PhpFile) return false

    return try {
        this.getRootPhpPsiElements()
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

fun PsiFile.getRootPhpPsiElements(): List<PhpPsiElement> {
    if (this !is PhpFile) return listOf()

    val element = this.firstChild

    return element.children.filterIsInstance<PhpNamespace>()
        .mapNotNull { it.statements }
        .getOrElse(
            0
        ) { element }
        .children
        .filterIsInstance<Statement>()
        .mapNotNull { it.firstChild }
        .filterIsInstance<PhpPsiElement>()
}

/**
 * Checks if the file is the `tests/Pest.php` file.
 */
fun PsiFile.isPestFile(): Boolean {
    val projectDir = this.project.guessProjectDir() ?: return false

    val pestFilePath = PestSettings.getInstance(this.project).pestFilePath

    return this.virtualFile?.path == projectDir.path + "/" + pestFilePath
}
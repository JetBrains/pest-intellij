package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager

@Suppress("TooGenericExceptionCaught", "SwallowedException")
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

fun PsiFile.isPestDatasetFile(): Boolean {
    if (this !is PhpFile) return false

    val element = this.firstChild

    return element.children.filterIsInstance<PhpNamespace>()
        .mapNotNull { it.statements }
        .getOrElse(
            0
        ) { element }
        .children
        .filterIsInstance<Statement>()
        .mapNotNull { it.firstChild }
        .any(PsiElement::isPestDataset)
}

fun PsiElement?.isPestDataset(): Boolean {
    return when (this) {
        null -> false
        is FunctionReferenceImpl -> this.isPestDatasetFunction()
        else -> false
    }
}

fun FunctionReferenceImpl.isPestDatasetFunction(): Boolean {
    return this.canonicalText in setOf("dataset")
}

/**
 * Checks if the file is the `tests/Pest.php` file.
 */
fun PsiFile.isPestFile(): Boolean {
    val projectDir = this.project.guessProjectDir() ?: return false

    val pestFilePath = PestSettings.getInstance(this.project).pestFilePath

    return this.virtualFile?.path == projectDir.path + "/" + pestFilePath
}
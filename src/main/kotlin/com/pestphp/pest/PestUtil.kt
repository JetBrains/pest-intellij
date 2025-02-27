package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.composer.configData.ComposerConfigManager
import com.jetbrains.php.lang.psi.PhpExpressionCodeFragment
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.indexers.key

val PEST_TEST_FILE_KEY = Key<CachedValue<Boolean>>("isPestTestFile")
val PEST_TEST_FILE_SMART_KEY = Key<CachedValue<Boolean>>("smart isPestTestFile")

fun PsiFile.isPestTestFile(isSmart: Boolean = false): Boolean {
    if (this !is PhpFile || this is PhpExpressionCodeFragment) return false
    return CachedValuesManager.getCachedValue(this, if (isSmart) PEST_TEST_FILE_SMART_KEY else PEST_TEST_FILE_KEY) {
        val isPestTestFile = this.getRootPhpPsiElements().any { psiElement -> psiElement.isPestTestReference(isSmart) }
        CachedValueProvider.Result.create(isPestTestFile, this)
    }
}

fun PsiFile.isIndexedPestTestFile(): Boolean {
    return FileBasedIndex.getInstance().getValues(
        key,
        this.realPath,
        ProjectScope.getProjectScope(this.project)
    ).isNotEmpty() && this.isPestTestFile(isSmart = true)
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
    val baseDir = getBaseDir(this.project, this.virtualFile) ?: return false

    val pestFilePath = PestSettings.getInstance(this.project).pestFilePath

    return this.virtualFile?.path == baseDir.path + "/" + pestFilePath
}

fun getBaseDir(project: Project, virtualFile: VirtualFile? = null): VirtualFile? {
    return ComposerConfigManager.getInstance(project).getConfig(virtualFile)?.parent
        ?: project.guessProjectDir()
}
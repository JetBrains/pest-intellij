package com.pestphp.pest.features.customExpectations

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.PhpFile

/**
 * Adds all methods to the expectation file service which has been indexed already.
 */
class CustomExpectationStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val expectationFileService = project.service<ExpectationFileService>()
        val psiManager = PsiManager.getInstance(project)

        project.service<DumbService>().runReadActionInSmartMode {
            fileBasedIndex
                .getAllKeys(CustomExpectationIndex.key, project)
                .mapNotNull {
                    fileBasedIndex.getContainingFiles(
                        CustomExpectationIndex.key,
                        it,
                        GlobalSearchScope.allScope(project)
                    ).firstOrNull()
                }
                .associate {
                    val psiFile = psiManager.findFile(it) as PhpFile

                    val values = fileBasedIndex.getFileData(
                        CustomExpectationIndex.key,
                        it,
                        project
                    ).values.flatten()

                    psiFile to values
                }
                .forEach {
                    expectationFileService.updateExtends(
                        it.key,
                        it.value
                    )
                }
            expectationFileService.generateFile { }
        }
    }
}
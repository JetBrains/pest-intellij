package com.pestphp.pest.customExpectations

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.PhpFile

/**
 * Adds all methods to the expectation file service which has been indexed already.
 */
class CustomExpectationStartupActivity: StartupActivity, StartupActivity.Background {
    override fun runActivity(project: Project) {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val expectationFileService = project.service<ExpectationFileService>()
        val psiManager = PsiManager.getInstance(project)

        runReadAction {
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
            expectationFileService.generateFile {  }
        }

    }
}
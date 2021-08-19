package com.pestphp.pest.startupActivity

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.expectExtends
import com.pestphp.pest.isPestEnabled
import com.pestphp.pest.services.ExpectationFileService

/**
 * Finds all `expect->extend` and generate our phpstorm helper file.
 */
class ExpectationFileStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        if(! project.isPestEnabled()) {
            return
        }

        val expectationFileService = project.service<ExpectationFileService>()

        val searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes(
            GlobalSearchScope.allScope(project),
            PhpFileType.INSTANCE,
        )

        ApplicationManager.getApplication().executeOnPooledThread {
            runReadAction {
                PsiSearchHelper.getInstance(project)
                    .processAllFilesWithWord(
                        "expect()->extend(",
                        searchScope,
                        {
                            if (it !is PhpFile) {
                                return@processAllFilesWithWord true
                            }

                            if (! it.isValid) {
                                return@processAllFilesWithWord true
                            }

                            if (it.expectExtends.isEmpty()) {
                                return@processAllFilesWithWord true
                            }

                            expectationFileService. updateExtends(it)

                            true
                        },
                        true
                    )

                expectationFileService.generateFile()
            }
        }
    }
}

package com.pestphp.pest.startupActivity

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.expectExtends
import com.pestphp.pest.services.ExpectationFileService

class ExpectationFileStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        val expectationFileService = project.service<ExpectationFileService>()

        val searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes(
            GlobalSearchScope.allScope(project),
            PhpFileType.INSTANCE,
        )

        ApplicationManager.getApplication().runReadAction {
            PsiSearchHelper.getInstance(project)
                .processAllFilesWithWord(
                    "expect()->extend(",
                    searchScope,
                    {
                        if (it !is PhpFile) {
                            return@processAllFilesWithWord true
                        }

                        if (it.expectExtends.isEmpty()) {
                            return@processAllFilesWithWord true
                        }

                        expectationFileService.updateExtends(it)

                        true
                    },
                    true
                )

            expectationFileService.generateFile()
        }
    }
}

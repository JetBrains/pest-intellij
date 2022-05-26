package com.pestphp.pest.customExpectations

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.customExpectations.generators.Method

class CustomExpectationListener(private val project: Project) : CustomExpectationNotifier {
    override fun changedExpectation(file: PsiFile, customExpectations: List<Method>) {
        val expectationFileService = project.service<ExpectationFileService>()

        if (file !is PhpFile) {
            return
        }

        val changedExpectations = expectationFileService.updateExtends(
            file,
            customExpectations
        )

        if (!changedExpectations) {
            return
        }

        invokeLater {
            expectationFileService.generateFile {
            }
        }
    }
}
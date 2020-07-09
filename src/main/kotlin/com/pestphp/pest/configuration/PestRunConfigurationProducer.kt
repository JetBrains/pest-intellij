package com.pestphp.pest.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.Function
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.testFramework.run.PhpTestConfigurationProducer
import com.pestphp.pest.PestUtil

class PestRunConfigurationProducer : PhpTestConfigurationProducer<PestRunConfiguration?>(
        PestRunConfiguration.VALIDATOR,
        FILE_TO_SCOPE,
        METHOD_NAMER,
        METHOD
) {
    override fun getConfigurationFactory(): ConfigurationFactory = PestRunConfigurationType.getInstance()

    override fun isEnabled(project: Project): Boolean = PestUtil.isEnabled(project)

    override fun getWorkingDirectory(element: PsiElement): VirtualFile? {
        if (element is PsiDirectory) {
            return element.parentDirectory?.virtualFile
        }

        return element.containingFile?.containingDirectory?.virtualFile
    }

    companion object {
        private val METHOD = Condition<PsiElement> { element: PsiElement? ->
            (element is FunctionReference
                    && PestUtil.isPestTestFunction((element as FunctionReference?)!!))
        }
        private val METHOD_NAMER = Function<PsiElement, String?> { element: PsiElement? ->
            if (element is FunctionReference) {
                PestUtil.getTestName(element)
            }
            null
        }
        private val FILE_TO_SCOPE = Function<PsiFile, PsiElement?> { file: PsiFile? ->
            if (PestUtil.isPestTestFile(file)) {
                return@Function file
            }
            null
        }
    }
}
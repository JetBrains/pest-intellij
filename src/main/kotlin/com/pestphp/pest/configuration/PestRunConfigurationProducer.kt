package com.pestphp.pest.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.Function
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.testFramework.run.PhpDefaultTestRunnerSettingsValidator
import com.jetbrains.php.testFramework.run.PhpDefaultTestRunnerSettingsValidator.PhpTestMethodFinder
import com.jetbrains.php.testFramework.run.PhpTestConfigurationProducer
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestEnabled
import com.pestphp.pest.isPestTestFile
import com.pestphp.pest.isPestTestFunction
import com.pestphp.pest.isPestConfigurationFile

class PestRunConfigurationProducer : PhpTestConfigurationProducer<PestRunConfiguration?>(
    VALIDATOR,
    FILE_TO_SCOPE,
    METHOD_NAMER,
    METHOD
) {
    override fun getConfigurationFactory(): ConfigurationFactory = PestRunConfigurationType.getInstance()

    override fun isEnabled(project: Project): Boolean = project.isPestEnabled()

    override fun getWorkingDirectory(element: PsiElement): VirtualFile? {
        if (element is PsiDirectory) {
            return element.parentDirectory?.virtualFile
        }

        return element.containingFile?.containingDirectory?.virtualFile
    }

    companion object {
        val METHOD = Condition<PsiElement> { element: PsiElement? ->
            return@Condition element.isPestTestFunction()
        }
        private val METHOD_NAMER = Function<PsiElement, String?> { element: PsiElement? ->
            return@Function element.getPestTestName()
        }
        private val FILE_TO_SCOPE = Function<PsiFile, PsiElement?> { file: PsiFile ->
            if (file.isPestTestFile()) {
                return@Function file
            }
            null
        }
        val VALIDATOR = PhpDefaultTestRunnerSettingsValidator(
            setOf<FileType>(PhpFileType.INSTANCE, XmlFileType.INSTANCE).toList(),
            PhpTestMethodFinder { file: PsiFile, _: String ->
                if (file.isPestConfigurationFile()) {
                    return@PhpTestMethodFinder true
                }

                file.isPestTestFile()
            },
            false,
            false
        )
    }
}

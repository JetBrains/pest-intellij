package com.pestphp.pest.configuration

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.configurations.RuntimeConfigurationWarning
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.PathUtil
import com.intellij.util.TextFieldCompletionProvider
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.run.PhpRunUtil
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings
import com.jetbrains.php.testFramework.run.PhpTestRunnerConfigurationEditor
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestIcons
import com.pestphp.pest.configuration.PestRunConfigurationProducer.Companion.VALIDATOR
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestFunction
import com.pestphp.pest.runner.PestConsoleProperties
import java.util.EnumMap

class PestRunConfiguration(project: Project, factory: ConfigurationFactory) : PhpTestRunConfiguration(
    project,
    factory,
    PestBundle.message("FRAMEWORK_NAME"),
    PestFrameworkType.instance,
    VALIDATOR,
    PestRunConfigurationHandler.instance,
    PestVersionDetector.instance
) {
    override fun createSettings(): PhpTestRunConfigurationSettings {
        return PestRunConfigurationSettings()
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        val names = EnumMap<PhpTestRunnerSettings.Scope, String>(PhpTestRunnerSettings.Scope::class.java)
        val editor = this.getConfigurationEditor(names)
        editor.setRunnerOptionsDocumentation("https://pestphp.com/docs/installation")
        return editor
    }

    @Throws(ExecutionException::class)
    fun checkAndGetState(env: ExecutionEnvironment, command: PhpCommandSettings): RunProfileState? {
        try {
            checkConfiguration()
        } catch (ignored: RuntimeConfigurationWarning) {
        } catch (var5: RuntimeConfigurationException) {
            throw ExecutionException(var5.message + " for " + this.name + " run-configuration")
        }
        return this.getState(env, command, null)
    }

    override fun createMethodFieldCompletionProvider(
        editor: PhpTestRunnerConfigurationEditor
    ): TextFieldCompletionProvider {
        return object : TextFieldCompletionProvider() {
            override fun addCompletionVariants(text: String, offset: Int, prefix: String, result: CompletionResultSet) {
                val file = PhpRunUtil.findPsiFile(project, settings.runnerSettings.filePath)
                PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
                    .asSequence()
                    .filter(FunctionReferenceImpl::isPestTestFunction)
                    .mapNotNull { it.getPestTestName() }
                    .map { LookupElementBuilder.create(it).withIcon(PestIcons.FILE) }
                    .forEach { result.addElement(it) }
            }
        }
    }

    override fun createRerunAction(
        consoleView: ConsoleView,
        properties: SMTRunnerConsoleProperties
    ): AbstractRerunFailedTestsAction? {
        return PestRerunFailedTestsAction(consoleView, properties)
    }

    override fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties {
        return PestConsoleProperties(this, executor)
    }

    override fun suggestedName(): String? {
        val runner = this.settings.runnerSettings
        return when (val scope = runner.scope) {
            PhpTestRunnerSettings.Scope.Directory -> PathUtil.getFileName(StringUtil.notNullize(runner.directoryPath))
            PhpTestRunnerSettings.Scope.File -> PathUtil.getFileName(StringUtil.notNullize(runner.filePath))
            PhpTestRunnerSettings.Scope.Method -> {
                val builder = StringBuilder()
                val file = PathUtil.getFileName(StringUtil.notNullize(runner.filePath))
                builder.append(file)
                builder.append("::")
                builder.append(runner.methodName)
                builder.toString()
            }
            PhpTestRunnerSettings.Scope.ConfigurationFile -> PathUtil.getFileName(
                StringUtil.notNullize(runner.configurationFilePath)
            )
            else -> {
                assert(false) { "Unknown scope: $scope" }
                null
            }
        }
    }
}

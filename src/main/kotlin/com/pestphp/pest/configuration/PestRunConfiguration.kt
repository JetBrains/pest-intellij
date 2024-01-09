package com.pestphp.pest.configuration

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.util.PathUtil
import com.intellij.util.TextFieldCompletionProvider
import com.jetbrains.php.PhpBundle
import com.jetbrains.php.config.commandLine.PhpCommandLinePathProcessor
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.run.PhpRunUtil
import com.jetbrains.php.run.remote.PhpRemoteInterpreterManager
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings
import com.jetbrains.php.testFramework.run.PhpTestRunnerConfigurationEditor
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings
import com.pestphp.pest.*
import com.pestphp.pest.configuration.PestRunConfigurationProducer.Companion.VALIDATOR
import com.pestphp.pest.runner.PestConsoleProperties
import java.util.*
import kotlin.io.path.Path

class PestRunConfiguration(project: Project, factory: ConfigurationFactory) : PhpTestRunConfiguration(
    project,
    factory,
    PestBundle.message("FRAMEWORK_NAME"),
    PestFrameworkType.instance,
    VALIDATOR,
    PestRunConfigurationHandler.instance,
    null // PestVersionDetector.instance
) {
    override fun createSettings(): PestRunConfigurationSettings {
        return PestRunConfigurationSettings()
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        val names = EnumMap<PhpTestRunnerSettings.Scope, String>(PhpTestRunnerSettings.Scope::class.java)
        val editor = this.getConfigurationEditor(names)

        editor.setRunnerOptionsDocumentation("https://pestphp.com/docs/installation")
        return PestTestRunConfigurationEditor(editor, this)
    }

    @Throws(ExecutionException::class)
    @Suppress("SwallowedException")
    fun checkAndGetState(env: ExecutionEnvironment, command: PhpCommandSettings): RunProfileState? {
        try {
            checkConfiguration()
        } catch (ignored: RuntimeConfigurationWarning) {
        } catch (exception: RuntimeConfigurationException) {
            throw ExecutionException(PestBundle.message("RUNTIME_CONFIGURATION_EXCEPTION_MESSAGE", exception.localizedMessage, this.name))
        }
        return this.getState(env, command, null)
    }

    override fun createMethodFieldCompletionProvider(
        editor: PhpTestRunnerConfigurationEditor
    ): TextFieldCompletionProvider {
        return object : TextFieldCompletionProvider() {
            override fun addCompletionVariants(text: String, offset: Int, prefix: String, result: CompletionResultSet) {
                val file = PhpRunUtil.findPsiFile(project, settings.runnerSettings.filePath)
                file?.getPestTests()
                    ?.mapNotNull { it.getPestTestName() }
                    ?.map { LookupElementBuilder.create(it).withIcon(PestIcons.File) }
                    ?.forEach { result.addElement(it) }
            }
        }
    }

    override fun createRerunAction(
        consoleView: ConsoleView,
        properties: SMTRunnerConsoleProperties
    ): AbstractRerunFailedTestsAction {
        return PestRerunFailedTestsAction(consoleView, properties)
    }

    override fun createTestConsoleProperties(executor: Executor): SMTRunnerConsoleProperties {
        val manager = PhpRemoteInterpreterManager.getInstance()


        val pathProcessor = when (this.interpreter?.isRemote) {
            true -> manager?.createPathMapper(this.project, interpreter!!.phpSdkAdditionalData)
            else -> null
        }

        return this.createTestConsoleProperties(
            executor,
            pathProcessor ?: PhpCommandLinePathProcessor.LOCAL
        )
    }

    private fun createTestConsoleProperties(
        executor: Executor,
        processor: PhpCommandLinePathProcessor
    ): PestConsoleProperties {
        val pathMapper = processor.createPathMapper(this.project)
        return PestConsoleProperties(
            this,
            executor,
            PestLocationProvider(pathMapper, this.project, this.getConfigurationFileRootPath())
        )
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

    fun applyTestArguments(command: PhpCommandSettings, coverageArguments: List<String>) {
        val config = PhpTestFrameworkSettingsManager.getInstance(project)
            .getOrCreateByInterpreter(PestFrameworkType.instance, interpreter, true)
            ?: throw ExecutionException("Could not find php interpreter.")

        val version = null

        val workingDirectory = getWorkingDirectory(project, settings, config)
            ?: throw ExecutionException(PhpBundle.message("php.interpreter.base.configuration.working.directory"))

        PestRunConfigurationHandler.instance.prepareCommand(
            project,
            command,
            config.executablePath!!,
            version
        )

        command.importCommandLineSettings(settings.commandLineSettings, workingDirectory)
        fillTestRunnerArguments(
            project,
            workingDirectory,
            settings.runnerSettings,
            coverageArguments,
            command,
            config,
            PestRunConfigurationHandler.instance
        )
    }

    override fun createCommand(
        interpreter: PhpInterpreter,
        env: MutableMap<String, String>,
        arguments: MutableList<String>,
        withDebugger: Boolean
    ): PhpCommandSettings {
        PestRunConfigurationHandler.instance.rootPath = getConfigurationFileRootPath()
        return super.createCommand(interpreter, env, arguments, withDebugger)
    }

    override fun getWorkingDirectory(
        project: Project,
        settings: PhpTestRunConfigurationSettings,
        config: PhpTestFrameworkConfiguration?
    ): String? {
        val cli = settings.commandLineSettings
        if (cli.workingDirectory?.isNotEmpty() == true) {
            return cli.workingDirectory
        }

        val configFileRootPath = getConfigurationFileRootPath()

        if (configFileRootPath.isNullOrEmpty()) {
            return super.getWorkingDirectory(project, settings, config)
        }

        return configFileRootPath
    }

    private fun getConfigurationFileRootPath(): String? {
        val configFile = getConfigurationFile(
            settings.runnerSettings,
            PhpTestFrameworkSettingsManager.getInstance(project)
                .getOrCreateByInterpreter(PestFrameworkType.instance, interpreter, true)
        ) ?: return null

        return VfsUtil.findFile(Path(configFile), false)?.parent?.path
    }

    val pestSettings: PestRunConfigurationSettings
        get() {
            return super.getSettings() as PestRunConfigurationSettings
        }
}

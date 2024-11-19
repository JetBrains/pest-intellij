package com.pestphp.pest.features.parallel

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.testframework.sm.runner.SMTestProxy.SMRootTestProxy
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.notification.NotificationType
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.NlsSafe
import com.jetbrains.php.PhpBundle
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.commandLine.PhpCommandSettingsBuilder
import com.jetbrains.php.phpunit.PhpUnitUtil
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.configuration.PestRerunProfile
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestVersionDetector
import com.pestphp.pest.statistics.PestUsagesCollector

internal val PEST_PARALLEL_ARGUMENTS = mutableListOf("--parallel", "--log-teamcity", "php://stdout")

class PestParallelProgramRunner : GenericProgramRunner<RunnerSettings>() {
    companion object {
        const val RUNNER_ID: String = "PestParallelRunner"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
        executorId == PestParallelTestExecutor.EXECUTOR_ID && profile is PestRunConfiguration

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        PestUsagesCollector.logParallelTestExecution(environment.project)

        val executionResult = if (environment.runProfile is PestRerunProfile) {
            state.execute(environment.executor, this)
        } else {
            val runConfiguration = environment.runProfile as? PestRunConfiguration
                ?: throw ExecutionException(PestBundle.message("PEST_PARALLEL_IS_NOT_SUPPORTED_FOR_SELECTED_RUN_PROFILE"))
            val command = createPestParallelCommand(runConfiguration)
            runConfiguration.checkAndGetState(environment, command)?.execute(environment.executor, this)
        }
        if (executionResult == null) throw ExecutionException(PhpBundle.message("execution.result.is.null"))
        val contentDescriptor = RunContentBuilder(executionResult, environment).showRunContent(environment.contentToReuse)
        postprocessExecutionResult(contentDescriptor, environment, PestBundle.message("PARALLEL_TESTING_IS_SUPPORTED_FROM_VERSION_2"))
        return contentDescriptor
    }

    override fun getRunnerId(): String = RUNNER_ID

    fun getArguments(): MutableList<String> = PEST_PARALLEL_ARGUMENTS
}

internal fun createPestParallelCommand(runConfiguration: PestRunConfiguration): PhpCommandSettings {
    FileDocumentManager.getInstance().saveAllDocuments()
    val interpreter = runConfiguration.interpreter ?: throw ExecutionException(PhpCommandSettingsBuilder.getInterpreterNotFoundError())
    return runConfiguration.createCommand(
        interpreter,
        mutableMapOf(),
        if (executeInParallel(runConfiguration)) mutableListOf() else PEST_PARALLEL_ARGUMENTS,
        false
    )
}

internal fun postprocessExecutionResult(
    contentDescriptor: RunContentDescriptor,
    environment: ExecutionEnvironment,
    @NlsSafe versionRequirement: String,
) {
    val processHandler = contentDescriptor.processHandler
    processHandler?.addProcessListener(object : ProcessAdapter() {
        override fun processTerminated(event: ProcessEvent) {
            val executionConsole = contentDescriptor.executionConsole as? SMTRunnerConsoleView ?: return
            val rootProxy = executionConsole.resultsViewer.root as? SMRootTestProxy ?: return
            if (rootProxy.isEmptySuite && !rootProxy.isTestsReporterAttached) {
                handleEmptySuite(environment, versionRequirement)
            }
        }
    })
}

private fun handleEmptySuite(
    environment: ExecutionEnvironment,
    @NlsSafe versionRequirement: String,
) {
    val profile = environment.runProfile as PestRunConfiguration
    val project = profile.project
    val interpreter = profile.interpreter ?: return
    val config = PhpTestFrameworkSettingsManager.getInstance(project).getOrCreateByInterpreter(
        PestFrameworkType.instance, interpreter, profile.getBaseFile(null, interpreter), true
    ) ?: return

    val version = if (!interpreter.isRemote) {
        PestVersionDetector.instance.getVersion(project, interpreter, config.executablePath)
    } else {
        null
    }

    createAndShowNotification(environment, versionRequirement, version)
}

private fun createAndShowNotification(
    environment: ExecutionEnvironment,
    @NlsSafe versionRequirement: String,
    version: String?,
) {
    PhpUnitUtil.NOTIFICATION_GROUP.createNotification(
        versionRequirement,
        NotificationType.ERROR
    ).apply {
        version?.let {
            setTitle(PestBundle.message("CURRENT_PEST_VERSION_IS", it))
        }
        setSuggestionType(true)
        notify(environment.project)
    }
}

internal fun executeInParallel(runConfiguration: RunConfiguration): Boolean {
    return runConfiguration is PestRunConfiguration && runConfiguration.pestSettings.runnerSettings.isParallelTestingEnabled
}

internal fun addParallelArguments(runConfiguration: PestRunConfiguration, command: PhpCommandSettings) {
    if (executeInParallel(runConfiguration)) {
        PestUsagesCollector.logParallelTestExecution(runConfiguration.project)
        command.addArguments(PEST_PARALLEL_ARGUMENTS)
    }
}
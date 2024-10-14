package com.pestphp.pest.features.parallel

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.jetbrains.php.PhpBundle
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.commandLine.PhpCommandSettingsBuilder
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.jetbrains.php.testFramework.PhpTestFrameworkVersionCache
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.statistics.PestUsagesCollector

private val PEST_PARALLEL_ARGUMENTS = mutableListOf("--parallel", "--log-teamcity", "php://stdout")
private const val PEST_PARALLEL_SUPPORT_MAJOR_VERSION = 2

class PestParallelProgramRunner : GenericProgramRunner<RunnerSettings>() {
    companion object {
        const val RUNNER_ID: String = "PestParallelRunner"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        if (executorId != PestParallelTestExecutor.EXECUTOR_ID || profile !is PestRunConfiguration) {
            return false
        }

        val project = profile.project
        val interpreter = profile.interpreter
        val config = PhpTestFrameworkSettingsManager.getInstance(project).getOrCreateByInterpreter(
            PestFrameworkType.instance, interpreter, profile.getBaseFile(null, interpreter), true
        ) ?: return false
        val version = PhpTestFrameworkVersionCache.getCachedVersion(project, config)

        return version == null || version.isOrGreaterThan(PEST_PARALLEL_SUPPORT_MAJOR_VERSION)
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        PestUsagesCollector.logParallelTestExecution(environment.project)
        val runConfiguration = environment.runProfile as? PestRunConfiguration
        if (runConfiguration == null) throw ExecutionException(PestBundle.message("PEST_PARALLEL_IS_NOT_SUPPORTED_FOR_SELECTED_RUN_PROFILE"))
        val command = createPestParallelCommand(runConfiguration)
        val executionResult = runConfiguration.checkAndGetState(environment, command)?.execute(environment.executor, this)
        if (executionResult == null) throw ExecutionException(PhpBundle.message("execution.result.is.null"))
        return RunContentBuilder(executionResult, environment).showRunContent(environment.contentToReuse)
    }

    override fun getRunnerId(): String {
        return RUNNER_ID
    }
}

internal fun createPestParallelCommand(runConfiguration: PestRunConfiguration): PhpCommandSettings {
    FileDocumentManager.getInstance().saveAllDocuments()
    val interpreter = runConfiguration.interpreter ?: throw ExecutionException(PhpCommandSettingsBuilder.getInterpreterNotFoundError())
    return runConfiguration.createCommand(
        interpreter,
        mutableMapOf(),
        PEST_PARALLEL_ARGUMENTS,
        false
    )
}
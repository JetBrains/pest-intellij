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
import com.pestphp.pest.PestBundle
import com.pestphp.pest.configuration.PestRunConfiguration

private val PEST_PARALLEL_ARGUMENTS = mutableListOf("--parallel", "--log-teamcity", "php://stdout")

class PestParallelProgramRunner : GenericProgramRunner<RunnerSettings>() {
    companion object {
        const val RUNNER_ID: String = "PestParallelRunner"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == PestParallelTestExecutor.EXECUTOR_ID && profile is PestRunConfiguration
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
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
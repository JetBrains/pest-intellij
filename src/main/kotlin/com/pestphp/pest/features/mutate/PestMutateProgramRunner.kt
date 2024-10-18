package com.pestphp.pest.features.mutate

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunContentDescriptor
import com.pestphp.pest.PestBundle
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.coverage.PestCoverageProgramRunner
import com.pestphp.pest.features.parallel.postprocessExecutionResult
import com.pestphp.pest.statistics.PestUsagesCollector

private val MUTATE_ARGUMENTS: MutableList<String> = mutableListOf("--mutate")

open class PestMutateProgramRunner : PestCoverageProgramRunner() {
    companion object {
        const val RUNNER_ID: String = "PestMutateRunner"
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        PestUsagesCollector.logMutationTestExecution(environment.project)
        val contentDescriptor = super.doExecute(state, environment)
        if (contentDescriptor != null) {
            postprocessExecutionResult(contentDescriptor, environment, PestBundle.message("MUTATION_TESTING_IS_AVAILABLE_FROM_VERSION_3"))
        }
        return contentDescriptor
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
        executorId == PestMutateTestExecutor.EXECUTOR_ID && profile is PestRunConfiguration

    override fun createCoverageArguments(targetCoverage: String?): MutableList<String> = MUTATE_ARGUMENTS

    override fun getRunnerId(): String = RUNNER_ID
}
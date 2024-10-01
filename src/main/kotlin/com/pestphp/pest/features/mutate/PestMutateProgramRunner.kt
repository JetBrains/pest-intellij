package com.pestphp.pest.features.mutate

import com.intellij.execution.configurations.RunProfile
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.coverage.PestCoverageProgramRunner

private val MUTATE_ARGUMENTS: MutableList<String> = mutableListOf("--mutate")

class PestMutateProgramRunner : PestCoverageProgramRunner() {
    companion object {
        const val RUNNER_ID: String = "PestMutateRunner"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == PestMutateTestExecutor.EXECUTOR_ID && profile is PestRunConfiguration
    }

    override fun createCoverageArguments(targetCoverage: String?): MutableList<String> = MUTATE_ARGUMENTS

    override fun getRunnerId(): String {
        return RUNNER_ID
    }
}
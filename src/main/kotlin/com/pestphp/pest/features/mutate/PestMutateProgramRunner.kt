package com.pestphp.pest.features.mutate

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunContentDescriptor
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.jetbrains.php.testFramework.PhpTestFrameworkVersionCache
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.coverage.PestCoverageProgramRunner
import com.pestphp.pest.statistics.PestUsagesCollector

private val MUTATE_ARGUMENTS: MutableList<String> = mutableListOf("--mutate")
private const val MUTATE_RELEASE_MAJOR_VERSION = 3

class PestMutateProgramRunner : PestCoverageProgramRunner() {
    companion object {
        const val RUNNER_ID: String = "PestMutateRunner"
    }

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        PestUsagesCollector.logMutationTestExecution(environment.project)
        return super.doExecute(state, environment)
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        if (executorId != PestMutateTestExecutor.EXECUTOR_ID || profile !is PestRunConfiguration) {
            return false
        }

        val project = profile.project
        val interpreter = profile.interpreter
        val config = PhpTestFrameworkSettingsManager.getInstance(project).getOrCreateByInterpreter(
            PestFrameworkType.instance, interpreter, profile.getBaseFile(null, interpreter), true
        ) ?: return false
        val version = PhpTestFrameworkVersionCache.getCachedVersion(project, config)

        return version == null || version.isOrGreaterThan(MUTATE_RELEASE_MAJOR_VERSION)
    }

    override fun createCoverageArguments(targetCoverage: String?): MutableList<String> = MUTATE_ARGUMENTS

    override fun getRunnerId(): String {
        return RUNNER_ID
    }
}
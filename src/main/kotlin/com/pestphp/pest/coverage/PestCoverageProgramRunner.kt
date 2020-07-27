package com.pestphp.pest.coverage

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.jetbrains.php.config.commandLine.PhpCommandSettingsBuilder
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.phpunit.coverage.PhpCoverageRunner
import com.jetbrains.php.phpunit.coverage.PhpUntCoverageProgramRunner
import com.jetbrains.php.run.PhpRunConfigurationHolder
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationSettings
import java.util.*

class PestCoverageProgramRunner : PhpCoverageRunner() {
    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == "Coverage" && profile is PestRunConfiguration
    }

    override fun createCoverageArguments(targetCoverage: String?): MutableList<String> {
        val coverageArguments: ArrayList<String> = ArrayList()
        coverageArguments.add("--coverage-clover")
        targetCoverage?.let { coverageArguments.add(it) }

        return coverageArguments
    }

    override fun getRunnerId(): String {
        return "PestCoverageRunner"
    }

    override fun createState(
        env: ExecutionEnvironment,
        interpreter: PhpInterpreter,
        runConfigurationHolder: PhpRunConfigurationHolder<*>,
        coverageArguments: MutableList<String>,
        localCoverage: String,
        targetCoverage: String
    ): RunProfileState {
        val runConfiguration = runConfigurationHolder.runConfiguration as PestRunConfiguration
        val settings = runConfigurationHolder.settings as PestRunConfigurationSettings
        val project = env.project

        val command = PhpCommandSettingsBuilder(project, interpreter).loadAndStartDebug(true).build()

        setAdditionalMapping(localCoverage, targetCoverage, command)
        return runConfiguration.checkAndGetState(env, command)
    }
}

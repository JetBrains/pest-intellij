package com.pestphp.pest.coverage

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.commandLine.PhpCommandSettingsBuilder
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.debug.xdebug.options.XdebugConfigurationOptionsManager
import com.jetbrains.php.phpunit.coverage.PhpCoverageRunner
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine.CoverageEngine
import com.jetbrains.php.run.PhpConfigurationOption
import com.jetbrains.php.run.PhpRunConfigurationHolder
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.features.parallel.addParallelArguments

open class PestCoverageProgramRunner : PhpCoverageRunner() {
    companion object {
        const val EXECUTOR_ID: String = "Coverage"
        const val RUNNER_ID: String = "PestCoverageRunner"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return executorId == EXECUTOR_ID && profile is PestRunConfiguration
    }

    override fun createCoverageArguments(targetCoverage: String?): MutableList<String> {
        val coverageArguments: ArrayList<String> = ArrayList()
        coverageArguments.add("--coverage-clover")
        targetCoverage?.let { coverageArguments.add(it) }

        return coverageArguments
    }

    override fun getRunnerId(): String = RUNNER_ID

    override fun createState(
        env: ExecutionEnvironment,
        interpreter: PhpInterpreter,
        runConfigurationHolder: PhpRunConfigurationHolder<*>,
        coverageArguments: MutableList<String>,
        localCoverage: String,
        targetCoverage: String
    ): RunProfileState? {
        val runConfiguration = runConfigurationHolder.runConfiguration as PestRunConfiguration

        val command = createPestCoverageCommand(runConfiguration, interpreter, coverageArguments, localCoverage, targetCoverage)

        return runConfiguration.checkAndGetState(env, command)
    }

    fun createPestCoverageCommand(
        runConfiguration: PestRunConfiguration,
        interpreter: PhpInterpreter,
        coverageArguments: List<String>,
        localCoverage: String,
        targetCoverage: String
    ): PhpCommandSettings {
        val command = PhpCommandSettingsBuilder(runConfiguration.project, interpreter)
            .loadDebugExtension().build().apply {
                runConfiguration.applyTestArguments(this, coverageArguments)
            }

        val options = when (runConfiguration.pestSettings.runnerSettings.coverageEngine) {
            CoverageEngine.XDEBUG -> XdebugConfigurationOptionsManager
                .getConfigurationOptionsProvider(runConfiguration.project, interpreter)
                .enableCoverage()
                .createXdebugConfigurations()
            CoverageEngine.PCOV -> listOf(PhpConfigurationOption("pcov.enabled", 1))
            else -> throw IllegalArgumentException("Unsupported coverage engine.")
        }
        command.addConfigurationOptions(options)
        addParallelArguments(runConfiguration, command)
        setAdditionalMapping(localCoverage, targetCoverage, command)

        return command
    }
}

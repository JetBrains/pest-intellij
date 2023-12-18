package com.pestphp.pest.coverage

import com.intellij.coverage.CoverageFileProvider
import com.intellij.coverage.CoverageRunner
import com.intellij.coverage.CoverageSuite
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import com.jetbrains.php.phpunit.coverage.PhpCoverageSuite
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine
import com.pestphp.pest.configuration.PestRunConfiguration

class PestCoverageEngine : PhpUnitCoverageEngine() {
    override fun isApplicableTo(conf: RunConfigurationBase<*>): Boolean {
        return conf is PestRunConfiguration
    }

    override fun createCoverageEnabledConfiguration(conf: RunConfigurationBase<*>): CoverageEnabledConfiguration {
        return PestCoverageEnabledConfiguration(conf as PestRunConfiguration)
    }

    override fun createCoverageSuite(
        covRunner: CoverageRunner,
        name: String,
        coverageDataFileProvider: CoverageFileProvider,
        config: CoverageEnabledConfiguration
    ): CoverageSuite? {
        if (config is PestCoverageEnabledConfiguration) {
            return PhpCoverageSuite(name, config.getConfiguration().project, covRunner, coverageDataFileProvider, config.createTimestamp())
        }

        return null
    }
}

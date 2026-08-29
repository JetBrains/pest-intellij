package com.intellij.pest.coverage

import com.intellij.coverage.CoverageRunner
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import com.intellij.php.coverage.PhpCoverageRunner
import com.pestphp.pest.configuration.PestRunConfiguration

class PestCoverageEnabledConfiguration(
    configuration: PestRunConfiguration
) : CoverageEnabledConfiguration(configuration, CoverageRunner.getInstance(PhpCoverageRunner::class.java)) {
    override fun coverageFileNameSeparator(): String = "@"
}

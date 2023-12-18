package com.pestphp.pest.coverage

import com.intellij.coverage.CoverageRunner
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageRunner
import com.pestphp.pest.configuration.PestRunConfiguration

class PestCoverageEnabledConfiguration(
    configuration: PestRunConfiguration
) : CoverageEnabledConfiguration(configuration, CoverageRunner.getInstance(PhpUnitCoverageRunner::class.java)) {
    override fun coverageFileNameSeparator(): String = "@"
}

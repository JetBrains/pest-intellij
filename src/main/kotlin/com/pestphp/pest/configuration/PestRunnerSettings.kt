package com.pestphp.pest.configuration

import com.intellij.util.xmlb.annotations.Attribute
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine.CoverageEngine
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings

class PestRunnerSettings : PhpTestRunnerSettings() {
    @Attribute("coverage_engine")
    var coverageEngine: CoverageEngine = CoverageEngine.XDEBUG
}

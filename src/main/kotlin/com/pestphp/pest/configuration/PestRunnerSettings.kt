package com.pestphp.pest.configuration

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine.CoverageEngine
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings

@Tag("PestRunner")
class PestRunnerSettings : PhpTestRunnerSettings() {
    @Attribute("coverage_engine")
    var coverageEngine: CoverageEngine = CoverageEngine.XDEBUG
    @Attribute("is_parallel_testing_enabled")
    var isParallelTestingEnabled: Boolean = false

    companion object {
        fun fromPhpTestRunnerSettings(settings: PhpTestRunnerSettings): PestRunnerSettings {
            val pestSettings = PestRunnerSettings()

            pestSettings.scope = settings.scope
            pestSettings.selectedType = settings.selectedType
            pestSettings.directoryPath = settings.directoryPath
            pestSettings.filePath = settings.filePath
            pestSettings.methodName = settings.methodName
            pestSettings.isUseAlternativeConfigurationFile = settings.isUseAlternativeConfigurationFile
            pestSettings.configurationFilePath = settings.configurationFilePath
            pestSettings.testRunnerOptions = settings.testRunnerOptions

            return pestSettings
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PestRunnerSettings) return false
        return super.equals(other) && coverageEngine == other.coverageEngine && isParallelTestingEnabled == other.isParallelTestingEnabled
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + coverageEngine.hashCode()
        result = 31 * result + isParallelTestingEnabled.hashCode()
        return result
    }
}

package com.pestphp.pest.configuration

import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings

class PestRunConfigurationSettings : PhpTestRunConfigurationSettings() {
    override fun createDefault(): PestRunnerSettings {
        return PestRunnerSettings()
    }

    override fun getRunnerSettings(): PhpTestRunnerSettings {
        return super.getRunnerSettings()
    }

    val pestRunnerSettings: PestRunnerSettings
        get() {
            val copy = PestRunnerSettings.fromPhpTestRunnerSettings(runnerSettings)
            this.runnerSettings = copy
            return copy
        }
}

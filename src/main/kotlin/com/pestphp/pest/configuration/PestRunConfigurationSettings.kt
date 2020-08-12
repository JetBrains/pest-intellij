package com.pestphp.pest.configuration

import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings

class PestRunConfigurationSettings : PhpTestRunConfigurationSettings() {
    override fun createDefault(): PhpTestRunnerSettings {
        return PestRunnerSettings()
    }
}

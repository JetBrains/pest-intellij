package com.pestphp.pest.configuration

import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings

class PestRunConfigurationSettings : PhpTestRunConfigurationSettings() {
    private val pestRunnerSettings = PestRunnerSettings()
}

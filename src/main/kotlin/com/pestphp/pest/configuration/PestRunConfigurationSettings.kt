package com.pestphp.pest.configuration

import com.intellij.util.xmlb.annotations.Property
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings

class PestRunConfigurationSettings : PhpTestRunConfigurationSettings() {
    override fun createDefault(): PestRunnerSettings {
        return PestRunnerSettings()
    }

    @Property(surroundWithTag = false)
    override fun getRunnerSettings(): PestRunnerSettings {
        return pestRunnerSettings
    }

    var pestRunnerSettings: PestRunnerSettings
        get() {
            if (super.getRunnerSettings() is PestRunnerSettings) {
                return super.getRunnerSettings() as PestRunnerSettings
            }

            val copy = PestRunnerSettings.fromPhpTestRunnerSettings(super.getRunnerSettings())
            super.setRunnerSettings(copy)
            return copy
        }
        set(value) = super.setRunnerSettings(value)

    override fun equals(other: Any?): Boolean {
        if (other !is PestRunConfigurationSettings) return false
        return super.equals(other) && pestRunnerSettings == other.pestRunnerSettings
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + pestRunnerSettings.hashCode()
        return result
    }
}

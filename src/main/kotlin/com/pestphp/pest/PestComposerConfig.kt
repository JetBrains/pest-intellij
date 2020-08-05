package com.pestphp.pest

import com.intellij.execution.configurations.ConfigurationType
import com.jetbrains.php.testFramework.PhpTestFrameworkComposerConfig
import com.pestphp.pest.configuration.PestRunConfigurationType.Companion.instance

class PestComposerConfig : PhpTestFrameworkComposerConfig(PestFrameworkType.instance, PACKAGE, RELATIVE_PATH) {
    override fun getDefaultConfigName(): String {
        return "phpunit.xml"
    }

    override fun getConfigurationType(): ConfigurationType {
        return instance
    }

    companion object {
        private const val PACKAGE = "pestphp/pest"
        private const val RELATIVE_PATH = "pestphp/pest/bin/pest"
    }
}

package com.pestphp.pest

import com.intellij.execution.configurations.ConfigurationType
import com.jetbrains.php.testFramework.PhpTestFrameworkComposerConfig
import com.pestphp.pest.configuration.PEST_RUN_CONFIGURATION_TYPE

class PestComposerConfig : PhpTestFrameworkComposerConfig(PEST_FRAMEWORK_TYPE, PACKAGE, RELATIVE_PATH) {
    override fun getDefaultConfigName(): String {
        return "phpunit.xml"
    }

    override fun getConfigurationType(): ConfigurationType {
        return PEST_RUN_CONFIGURATION_TYPE
    }

    companion object {
        private const val PACKAGE = "pestphp/pest"
        private const val RELATIVE_PATH = "pestphp/pest/bin/pest"
    }
}

package com.pestphp.pest;

import com.intellij.execution.configurations.ConfigurationType;
import com.jetbrains.php.testFramework.PhpTestFrameworkComposerConfig;
import com.pestphp.pest.configuration.PestRunConfigurationType;
import org.jetbrains.annotations.NotNull;

public class PestComposerConfig extends PhpTestFrameworkComposerConfig {
    private static final String PACKAGE = "pestphp/pest";
    private static final String RELATIVE_PATH = "pestphp/pest/bin/pest";

    public PestComposerConfig() {
        super(PestFrameworkType.getInstance(), PACKAGE, RELATIVE_PATH);
    }

    @Override
    protected @NotNull String getDefaultConfigName() {
        return "phpunit.xml";
    }

    @Override
    protected @NotNull ConfigurationType getConfigurationType() {
        return PestRunConfigurationType.getInstance();
    }
}

package com.pestphp.pest.configuration;

import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings;
import org.jetbrains.annotations.NotNull;

public class PestRunConfigurationSettings extends PhpTestRunConfigurationSettings {
    @NotNull private final PestRunnerSettings pestRunnerSettings = new PestRunnerSettings();
}

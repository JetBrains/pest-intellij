package com.pestphp.pest.configuration;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Property;
import com.jetbrains.php.run.PhpCommandLineSettings;
import com.jetbrains.php.run.PhpRunConfigurationSettings;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PestRunConfigurationSettings extends PhpTestRunConfigurationSettings {
    @NotNull private PestRunnerSettings pestRunnerSettings = new PestRunnerSettings();
}

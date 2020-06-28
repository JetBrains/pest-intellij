package com.pestphp.pest.configuration;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Property;
import com.jetbrains.php.run.PhpCommandLineSettings;
import com.jetbrains.php.run.PhpRunConfigurationSettings;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PestRunConfigurationSettings extends PhpTestRunConfigurationSettings {
    @Nullable private String testScope;

    @NotNull private PhpCommandLineSettings phpCommandLineSettings = new PhpCommandLineSettings();
    @NotNull private PestRunnerSettings pestRunnerSettings = new PestRunnerSettings();

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return phpCommandLineSettings.getWorkingDirectory();
    }

    @Override
    public void setWorkingDirectory(@Nullable String workingDirectory) {
        phpCommandLineSettings.setWorkingDirectory(workingDirectory);
    }

    @Property
    @NotNull
    public String getTestScope() {
        return StringUtil.notNullize(testScope);
    }

    public void setTestScope(@Nullable String testScope) {
        this.testScope = testScope;
    }

    public @NotNull PhpCommandLineSettings getPhpCommandLineSettings() {
        return phpCommandLineSettings;
    }

    public @NotNull PestRunnerSettings getPestRunnerSettings() {
        return pestRunnerSettings;
    }
}

package com.pestphp.pest.configuration;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpTestFrameworkVersionDetector;
import com.jetbrains.php.testFramework.PhpTestFrameworkType;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationHandler;
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettingsValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PhpTestRunConfiguration extends com.jetbrains.php.testFramework.run.PhpTestRunConfiguration {
    protected PhpTestRunConfiguration(Project project, ConfigurationFactory factory, String name, @NotNull PhpTestFrameworkType frameworkType, @NotNull PhpTestRunnerSettingsValidator validator, @NotNull PhpTestRunConfigurationHandler handler, @Nullable PhpTestFrameworkVersionDetector versionDetector) {
        super(project, factory, name, frameworkType, validator, handler, versionDetector);
    }

    @Override
    public void setBeforeRunTasks(@NotNull List<BeforeRunTask<?>> value) {
        super.setBeforeRunTasks(value);
    }
}

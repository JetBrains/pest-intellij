package com.pestphp.pest.configuration;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.TextFieldCompletionProvider;
import com.jetbrains.php.config.commandLine.PhpCommandLinePathProcessor;
import com.jetbrains.php.config.commandLine.PhpCommandSettings;
import com.jetbrains.php.testFramework.run.PhpTestRunConfiguration;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationEditor;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings;
import com.jetbrains.php.testFramework.run.PhpTestRunnerConfigurationEditor;
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings;
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings.Scope;
import com.pestphp.pest.PestFrameworkType;
import com.pestphp.pest.runner.PestConsoleProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class PestRunConfiguration extends PhpTestRunConfiguration {
    protected PestRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(
            project,
            factory,
            name,
            PestFrameworkType.getInstance(),
            PestRunConfigurationProducer.Companion.getVALIDATOR(),
            PestRunConfigurationHandler.instance,
            PestVersionDetector.getInstance()
        );
    }

    @NotNull
    @Override
    protected PhpTestRunConfigurationSettings createSettings() {
        return new PestRunConfigurationSettings();
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        EnumMap<Scope, String> names = new EnumMap<>(Scope.class);
        PhpTestRunConfigurationEditor editor = this.getConfigurationEditor(names);
        editor.setRunnerOptionsDocumentation("https://pestphp.com/docs/installation");
        return editor;
    }

    public RunProfileState checkAndGetState(@NotNull ExecutionEnvironment env, @NotNull PhpCommandSettings command) throws ExecutionException {
        try {
            this.checkConfiguration();
        } catch (RuntimeConfigurationWarning ignored) {
        } catch (RuntimeConfigurationException var5) {
            throw new ExecutionException(var5.getMessage() + " for " + this.getName() + " run-configuration");
        }

        return this.getState(env, command, null);
    }
    @Override
    protected @NotNull TextFieldCompletionProvider createMethodFieldCompletionProvider(@NotNull PhpTestRunnerConfigurationEditor editor) {
        return new TextFieldCompletionProvider() {
            @Override
            protected void addCompletionVariants(@NotNull String text, int offset, @NotNull String prefix, @NotNull CompletionResultSet result) {
                // TODO: add completions to results object.
            }
        };
    }

    @Override
    protected @Nullable AbstractRerunFailedTestsAction createRerunAction(@NotNull ConsoleView consoleView, @NotNull SMTRunnerConsoleProperties properties) {
        return new PestRerunFailedTestsAction(consoleView, properties);
    }

    @Override
    public @NotNull SMTRunnerConsoleProperties createTestConsoleProperties(@NotNull Executor executor) {
        return new PestConsoleProperties(this, executor);
    }

    @Nullable
    public String suggestedName() {
        PhpTestRunnerSettings runner = this.getSettings().getRunnerSettings();
        Scope scope = runner.getScope();
        switch(scope) {
            case Directory:
                return PathUtil.getFileName(StringUtil.notNullize(runner.getDirectoryPath()));
            case File:
                return PathUtil.getFileName(StringUtil.notNullize(runner.getFilePath()));
            case Method:
                StringBuilder builder = new StringBuilder();
                String file = PathUtil.getFileName(StringUtil.notNullize(runner.getFilePath()));
                builder.append(file);
                builder.append("::");
                builder.append(runner.getMethodName());
                return builder.toString();
            case ConfigurationFile:
                return PathUtil.getFileName(StringUtil.notNullize(runner.getConfigurationFilePath()));
            default:
                assert false : "Unknown scope: " + scope;

                return null;
        }
    }
}

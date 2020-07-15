package com.pestphp.pest.configuration;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.TextFieldCompletionProvider;
import com.jetbrains.php.testFramework.run.PhpTestRunConfiguration;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationEditor;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationSettings;
import com.jetbrains.php.testFramework.run.PhpTestRunnerConfigurationEditor;
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings.Scope;
import com.pestphp.pest.PestFrameworkType;
import org.jetbrains.annotations.NotNull;

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

    @Override
    protected @NotNull TextFieldCompletionProvider createMethodFieldCompletionProvider(@NotNull PhpTestRunnerConfigurationEditor editor) {
        return new TextFieldCompletionProvider() {
            @Override
            protected void addCompletionVariants(@NotNull String text, int offset, @NotNull String prefix, @NotNull CompletionResultSet result) {
                // TODO: add completions to results object.
            }
        };
    }

}

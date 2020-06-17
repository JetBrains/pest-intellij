package com.pestphp.pest.configuration.editor;

import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.run.PhpCommandLineConfigurationEditor;
import com.jetbrains.php.run.PhpRunConfigurationInterpreterCombobox;
import com.pestphp.pest.configuration.PestRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PestRunConfigurationEditor extends SettingsEditor<PestRunConfiguration> {
    @NotNull private final Project project;

    private JPanel panel;

    private JPanel pestSettingsPanel;
    private PestSettingsEditor pestSettingsEditor;

    private JPanel cliSettingsPanel;
    private PhpCommandLineConfigurationEditor phpCommandLineConfigurationEditor;

    public PestRunConfigurationEditor(@NotNull final Project project) {
        super();
        this.project = project;
        phpCommandLineConfigurationEditor = new PhpCommandLineConfigurationEditor();

        phpCommandLineConfigurationEditor.init(project, true);
    }

    @Override
    protected void resetEditorFrom(@NotNull PestRunConfiguration runConfiguration) {
        pestSettingsEditor.resetEditorFrom(runConfiguration.getSettings().getPestRunnerSettings());
        phpCommandLineConfigurationEditor.resetEditorFrom(runConfiguration.getSettings().getPhpCommandLineSettings());
    }

    @Override
    protected void applyEditorTo(@NotNull PestRunConfiguration runConfiguration) throws ConfigurationException {
        pestSettingsEditor.applyEditorTo(runConfiguration.getSettings().getPestRunnerSettings());
        phpCommandLineConfigurationEditor.applyEditorTo(runConfiguration.getSettings().getPhpCommandLineSettings());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }

    private void createUIComponents() {
        pestSettingsEditor = new PestSettingsEditor(project);
    }
}

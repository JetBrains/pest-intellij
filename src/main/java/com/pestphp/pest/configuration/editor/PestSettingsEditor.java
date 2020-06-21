package com.pestphp.pest.configuration.editor;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.pestphp.pest.configuration.PestRunnerSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PestSettingsEditor {
    private JPanel panel;
    private JBLabel fileLabel;
    private TextFieldWithBrowseButton fileInput;
    private Project project;

    public PestSettingsEditor(Project project) {
        this.project = project;
    }

    protected void resetEditorFrom(@NotNull PestRunnerSettings pestRunnerSettings) {
    }

    protected void applyEditorTo(@NotNull PestRunnerSettings pestRunnerSettings) throws ConfigurationException {
    }

    private void createUIComponents() {
        fileInput = new TextFieldWithBrowseButton();
        fileInput.addBrowseFolderListener(
            null,
            null,
            project,
            FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()
        );
    }

    public JPanel getPanel() {
        return panel;
    }
}

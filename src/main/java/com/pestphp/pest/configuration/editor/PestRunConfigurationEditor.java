package com.pestphp.pest.configuration.editor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.jetbrains.php.run.PhpCommandLineConfigurationEditor;
import com.pestphp.pest.configuration.PestRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PestRunConfigurationEditor extends SettingsEditor<PestRunConfiguration> {
    @NotNull private final Project project;

    private JPanel panel;

    private JPanel pestSettingsPanel;
    private PestSettingsEditor pestSettingsEditor;

    private PhpCommandLineConfigurationEditor phpCommandLineConfigurationEditor;

    public PestRunConfigurationEditor(@NotNull final Project project) {
        super();
        this.project = project;

        createUIComponents();
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
        panel = new JPanel();
        /*panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY),
            "some title"
        );*/
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        pestSettingsPanel = new JPanel();
        pestSettingsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY),
            "Pest Runner"
        ));
        pestSettingsEditor = new PestSettingsEditor(project);
        pestSettingsPanel.add(pestSettingsEditor.getPanel());

        panel.add(pestSettingsPanel);

        try {
            phpCommandLineConfigurationEditor = new PhpCommandLineConfigurationEditor();
            phpCommandLineConfigurationEditor.init(project);
            Method method = phpCommandLineConfigurationEditor.getClass().getMethod("$$$getRootComponent$$$");

            panel.add((Component) method.invoke(phpCommandLineConfigurationEditor));
        }  catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

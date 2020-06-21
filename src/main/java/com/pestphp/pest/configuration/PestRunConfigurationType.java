package com.pestphp.pest.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.run.PhpRunConfigurationFactoryBase;
import com.pestphp.pest.PestBundle;
import com.pestphp.pest.PestFrameworkType;import com.pestphp.pest.PestIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PestRunConfigurationType implements ConfigurationType {
    private final ConfigurationFactory factory = new PhpRunConfigurationFactoryBase(this, "Pest") {
        @NotNull
        public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new PestRunConfiguration(project, this, PestBundle.message("FRAMEWORK_NAME"));
        }
    };

    static PestRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(PestRunConfigurationType.class);
    }

    @Override
    public @NotNull @Nls(capitalization = Capitalization.Title) String getDisplayName() {
        return PestBundle.message("FRAMEWORK_NAME");
    }

    @Override
    public @Nls(capitalization = Capitalization.Sentence) String getConfigurationTypeDescription() {
        return PestBundle.message("FRAMEWORK_NAME");
    }

    @Override
    public Icon getIcon() {
        return PestIcons.CONFIG;
    }

    @Override
    public @NotNull String getId() {
        return "PestRunConfigurationType";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{this.factory};
    }
}

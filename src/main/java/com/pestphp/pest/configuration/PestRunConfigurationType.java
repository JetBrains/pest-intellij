package com.pestphp.pest.configuration;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.jetbrains.php.run.PhpRunConfigurationFactoryBase;
import com.pestphp.pest.PestBundle;
import com.pestphp.pest.PestFrameworkType;import com.pestphp.pest.PestIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PestRunConfigurationType extends SimpleConfigurationType implements DumbAware {
    protected PestRunConfigurationType() {
        super(
            "PestRunConfigurationType",
            PestBundle.message("FRAMEWORK_NAME"),
            PestBundle.message("FRAMEWORK_NAME"),
            NotNullLazyValue.createValue(() -> PestIcons.CONFIG));
    }

    public static PestRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(PestRunConfigurationType.class);
    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new PestRunConfiguration(project, this, PestBundle.message("FRAMEWORK_NAME"));
    }
}

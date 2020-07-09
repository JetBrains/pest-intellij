package com.pestphp.pest.configuration;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PestRunConfigurationProducer extends LazyRunConfigurationProducer<PestRunConfiguration> {
    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return PestRunConfigurationType.getInstance();
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull PestRunConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref<PsiElement> sourceElement) {
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull PestRunConfiguration configuration, @NotNull ConfigurationContext context) {
        return false;
    }
}

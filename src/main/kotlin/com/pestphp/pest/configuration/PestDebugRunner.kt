package com.pestphp.pest.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.jetbrains.php.testFramework.run.PhpTestDebugRunner;
import org.jetbrains.annotations.NotNull;

public class PestDebugRunner extends PhpTestDebugRunner<PestRunConfiguration> {
    protected PestDebugRunner() {
        super(PestRunConfiguration.class);
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull PestRunConfiguration pestRunConfiguration, @NotNull RunProfileState runProfileState, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return null;
    }

    @Override
    public @NotNull String getRunnerId() {
        return "PestDebugRunner";
    }
}

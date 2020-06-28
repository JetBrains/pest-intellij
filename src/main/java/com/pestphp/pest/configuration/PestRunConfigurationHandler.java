package com.pestphp.pest.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.php.PhpBundle;
import com.jetbrains.php.config.commandLine.PhpCommandSettings;
import com.jetbrains.php.run.PhpExecutionUtil;
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PestRunConfigurationHandler implements PhpTestRunConfigurationHandler {
    private static final Logger LOG = Logger.getInstance(PestRunConfigurationHandler.class);
    private static final PestRunConfigurationHandler instance = new PestRunConfigurationHandler();

    public static PestRunConfigurationHandler getInstance() {
        return instance;
    }

    @Override
    public @NotNull String getConfigFileOption() {
        return "--configuration";
    }

    @Override
    public void prepareCommand(@NotNull Project project, @NotNull PhpCommandSettings commandSettings, @NotNull String exe, @Nullable String version) throws ExecutionException {
        // String scriptFile = PhpExecutionUtil.loadHelperScriptAndGetText(project, "behat.php", commandSettings, PestRunConfigurationHandler.class);

        commandSettings.setScript(exe, false);
        commandSettings.addArgument("--teamcity");

        commandSettings.addEnv("IDE_PEST_EXE", exe);

        if (StringUtil.isNotEmpty(version)) {
            commandSettings.addEnv("IDE_PEST_VERSION", version);
        }
    }

    @Override
    public void runType(@NotNull Project project, @NotNull PhpCommandSettings phpCommandSettings, @NotNull String type, @NotNull String workingDirectory) throws ExecutionException {
        throw new ExecutionException("Can not run pest with type.");
    }

    @Override
    public void runDirectory(@NotNull Project project, @NotNull PhpCommandSettings phpCommandSettings, @NotNull String directory, @NotNull String workingDirectory) throws ExecutionException {
        phpCommandSettings.addPathArgument(directory);
    }

    @Override
    public void runFile(@NotNull Project project, @NotNull PhpCommandSettings phpCommandSettings, @NotNull String file, @NotNull String workingDirectory) throws ExecutionException {
        phpCommandSettings.addPathArgument(file);
    }

    @Override
    public void runMethod(@NotNull Project project, @NotNull PhpCommandSettings phpCommandSettings, @NotNull String filePath, @NotNull String methodName, @NotNull String workingDirectory) throws ExecutionException {
        throw new ExecutionException("Can not run pest with method.");
    }
}

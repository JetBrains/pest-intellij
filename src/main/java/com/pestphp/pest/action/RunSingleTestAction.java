package com.pestphp.pest.action;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiElement;
import com.pestphp.pest.PestBundle;
import com.pestphp.pest.PestIcons;
import org.jetbrains.annotations.NotNull;

public class RunSingleTestAction extends CreateRunSingleTestAction {
    public RunSingleTestAction(@NotNull PsiElement element, @NotNull String testName) {
        super(element, testName);

        getTemplatePresentation().setText(PestBundle.message(
            "RUN_SINGLE_TEST",
            testName
        ));
        getTemplatePresentation().setIcon(PestIcons.RUN_SINGLE_TEST);
    }

    protected boolean isTemporary() {
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        RunnerAndConfigurationSettings forRun = prepareAction();
        if (forRun == null) {
            return;
        }

        ProgramRunnerUtil.executeConfiguration(
            forRun,
            DefaultRunExecutor.getRunExecutorInstance()
        );
    }
}

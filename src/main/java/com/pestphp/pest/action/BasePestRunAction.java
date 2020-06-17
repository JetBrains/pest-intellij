package com.pestphp.pest.action;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.actions.runAnything.RunAnythingAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasePestRunAction extends RunAnythingAction {
    protected Project project;
    protected boolean wasCreated = false;

    BasePestRunAction(@NotNull Project project) {
        this.project = project;
    }

    protected Project getProject() {
        return project;
    }

    @Nullable
    abstract protected RunnerAndConfigurationSettings prepareAction();

    protected boolean isTemporary() {
        return false;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        prepareAction();
    }
}

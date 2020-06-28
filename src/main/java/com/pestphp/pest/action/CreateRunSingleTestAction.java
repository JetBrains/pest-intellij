package com.pestphp.pest.action;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.psi.PsiElement;
import com.pestphp.pest.PestBundle;
import com.pestphp.pest.PestIcons;
import com.pestphp.pest.PestUtil;
import com.pestphp.pest.configuration.PestRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class CreateRunSingleTestAction extends BasePestRunAction {
    private final String path;
    private final String testName;

    public CreateRunSingleTestAction(@NotNull PsiElement element,
                                     @NotNull String testName) {
        super(element.getProject());

        this.path = element.getContainingFile().getVirtualFile().getPath();
        this.testName = testName;

        getTemplatePresentation().setText(PestBundle.message(
            "CREATE_SINGLE_TEST",
            testName
        ));
        getTemplatePresentation().setIcon(PestIcons.CONFIG);
    }

    @Nullable
    protected RunnerAndConfigurationSettings prepareAction() {
        return null;
    }
}

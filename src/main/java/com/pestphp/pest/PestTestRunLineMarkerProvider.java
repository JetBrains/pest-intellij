package com.pestphp.pest;

import com.intellij.execution.TestStateStorage;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.execution.testframework.TestIconMapper;
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtils;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.pestphp.pest.action.CreateRunSingleTestAction;
import com.pestphp.pest.action.RunSingleTestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PestTestRunLineMarkerProvider extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement leaf) {
        if (!PhpPsiUtil.isOfType(leaf, PhpTokenTypes.IDENTIFIER)) {
            return null;
        }

        FunctionReference element = ObjectUtils.tryCast(leaf.getParent(), FunctionReference.class);

        if (element == null) {
            return null;
        }

        if (!PestUtil.isPestTestFunction(element)) {
            return null;
        }

        String testName = PestUtil.getTestName(element);

        if (testName == null) {
            testName = "Unknown";
        }

        AnAction[] actions = new AnAction[2];
        actions[0] = new RunSingleTestAction(element, testName);
        actions[1] = new CreateRunSingleTestAction(element, testName);


        // return RunLineMarkerContributor.withExecutorActions(getTestStateIcon(getLocationHint(testName), leaf.getProject(), false));
        return RunLineMarkerContributor.withExecutorActions(PestIcons.RUN_SINGLE_TEST);
        // return new Info(PestIcons.RUN_SINGLE_TEST, actions, RunLineMarkerContributor.RUN_TEST_TOOLTIP_PROVIDER);
    }
}

package com.pestphp.pest;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtils;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        // return RunLineMarkerContributor.withExecutorActions(getTestStateIcon(getLocationHint(testName), leaf.getProject(), false));
        return RunLineMarkerContributor.withExecutorActions(PestIcons.RUN_SINGLE_TEST);
    }
}

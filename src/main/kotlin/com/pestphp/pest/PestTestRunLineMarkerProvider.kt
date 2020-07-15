package com.pestphp.pest

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.pestphp.pest.PestUtil.isPestTestFunction

class PestTestRunLineMarkerProvider : RunLineMarkerContributor() {
    override fun getInfo(leaf: PsiElement): Info? {
        if (!PhpPsiUtil.isOfType(leaf, PhpTokenTypes.IDENTIFIER)) {
            return null
        }

        // return RunLineMarkerContributor.withExecutorActions(getTestStateIcon(getLocationHint(testName), leaf.getProject(), false));
        return when {
            leaf.parent !is FunctionReference -> null
            isPestTestFunction(leaf.parent as FunctionReference) -> withExecutorActions(PestIcons.RUN_SINGLE_TEST)
            else -> null
        }

    }
}
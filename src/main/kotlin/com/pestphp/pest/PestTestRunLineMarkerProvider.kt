package com.pestphp.pest

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

class PestTestRunLineMarkerProvider : RunLineMarkerContributor() {
    override fun getInfo(leaf: PsiElement): Info? {
        if (!PhpPsiUtil.isOfType(leaf, PhpTokenTypes.IDENTIFIER)) {
            return null
        }

        if ((leaf.parent as? FunctionReferenceImpl)?.isPestTestFunction() == true) {
            return withExecutorActions(PestIcons.RUN_SINGLE_TEST)
        }

        return null
    }
}

package com.pestphp.pest

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

class PestTestRunLineMarkerProvider : RunLineMarkerContributor() {
    override fun getInfo(leaf: PsiElement): Info? {
        if (leaf !is FunctionReferenceImpl || !leaf.isPestTestReference()) {
            return null
        }

        return withExecutorActions(PestIcons.RUN_SINGLE_TEST)
    }
}

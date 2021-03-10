package com.pestphp.pest

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons.RunConfigurations.TestState.Run
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

class PestTestRunLineMarkerProvider : RunLineMarkerContributor() {
    override fun getInfo(leaf: PsiElement): Info? {
        if (!PhpPsiUtil.isOfType(leaf, PhpTokenTypes.IDENTIFIER)) return null

        val reference = leaf.parent
        if (reference !is FunctionReferenceImpl || !reference.isPestTestReference()) return null

        val fqn = reference.toPestFqn()

        val icon = fqn.firstOrNull { getTestStateIcon(it, leaf.project, false) !== Run }
            ?.let { getTestStateIcon(it, leaf.project, false) } ?: Run

        return withExecutorActions(icon)
    }
}

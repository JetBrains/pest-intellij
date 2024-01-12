package com.pestphp.pest

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons.RunConfigurations.TestState.Run
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

/**
 * Adds markers on the line on the left side for running a pest specific pest test.
 */
class PestTestRunLineMarkerProvider : RunLineMarkerContributor() {
    override fun getInfo(leaf: PsiElement): Info? {
        // Handle icons if the reference is a pest test.
        if (isPestTestReference(leaf)) {
            return getPestTest(
                leaf.parent as FunctionReferenceImpl,
                leaf.project,
            )
        }

        // Handle icon for running all tests in the file.
        if (PhpPsiUtil.isOfType(leaf, PhpTokenTypes.PHP_OPENING_TAG) && leaf.containingFile.isPestTestFile()) {
            return withExecutorActions(PestIcons.Run)
        }

        return null
    }

    private fun isPestTestReference(leaf: PsiElement): Boolean {
        return if (PhpPsiUtil.isOfType(leaf, PhpTokenTypes.IDENTIFIER)) {
            leaf.parent is FunctionReferenceImpl && leaf.parent.isPestTestReference()
        } else {
            false
        }
    }

    private fun getPestTest(reference: FunctionReferenceImpl, project: Project): Info {
        val fqn = reference.toPestFqn()

        val icon = fqn.firstOrNull { getTestStateIcon(it, project, false) !== Run }
            ?.let { getTestStateIcon(it, project, false) } ?: Run

        return withExecutorActions(icon)
    }
}
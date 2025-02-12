package com.pestphp.pest

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons.RunConfigurations.TestState.Run
import com.intellij.icons.AllIcons.RunConfigurations.TestState.Run_run
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

/**
 * Adds markers on the line on the left side for running a pest specific pest test.
 */
class PestTestRunLineMarkerProvider : RunLineMarkerContributor() {
    override fun getInfo(leaf: PsiElement): Info? {
        if (!leaf.containingFile.isPestTestFile(isSmart = true)) {
            return null
        }

        // Handle icons if the reference is a pest test.
        if (isPestTestReference(leaf)) {
            return getPestTest(
                leaf.parent as FunctionReferenceImpl,
                leaf.project,
            )
        }

        // Handle icon for running all tests in the file.
        if (PhpPsiUtil.isOfType(leaf, PhpTokenTypes.PHP_OPENING_TAG)) {
            return withExecutorActions(Run_run)
        }

        return null
    }

    private fun isPestTestReference(leaf: PsiElement): Boolean {
        if (PhpPsiUtil.isOfType(leaf, PhpTokenTypes.IDENTIFIER)) {
            (leaf.parent as? FunctionReferenceImpl)?.let { functionReference ->
                if (!functionReference.isAnyPestFunction()) {
                    return false
                }
                val statementChild = PhpPsiUtil.getParentOfClass(functionReference, true, Statement::class.java)?.firstChild
                val outerFunctionReference = PhpPsiUtil.getParentByCondition<FunctionReferenceImpl>(
                    statementChild,
                    { it is FunctionReferenceImpl },
                    PhpFile.INSTANCEOF
                )
                if (outerFunctionReference == null || outerFunctionReference.isDescribeFunction()) {
                    return statementChild is FunctionReference && statementChild.isPestTestReference(isSmart = true)
                }
            }
        }
        return false
    }

    private fun getPestTest(reference: FunctionReferenceImpl, project: Project): Info {
        val fqn = reference.toPestFqn()

        val icon = fqn.firstOrNull { getTestStateIcon(it, project, false) !== Run }
            ?.let { getTestStateIcon(it, project, false) } ?: Run

        return withExecutorActions(icon)
    }
}
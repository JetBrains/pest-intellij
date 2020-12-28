package com.pestphp.pest.inspections

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.inspections.PhpExpressionResultUnusedInspection
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.isPestTestFunction

class SuppressExpressionResultUnusedInspection : InspectionSuppressor {
    companion object {
        private val SUPPRESSED_PHP_INSPECTIONS = listOf(PhpExpressionResultUnusedInspection().id)
    }

    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (element !is FunctionReferenceImpl) {
            return false
        }

        if (!element.isPestTestFunction()) {
            return false
        }

        return SUPPRESSED_PHP_INSPECTIONS.contains(toolId)
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return SuppressQuickFix.EMPTY_ARRAY
    }
}

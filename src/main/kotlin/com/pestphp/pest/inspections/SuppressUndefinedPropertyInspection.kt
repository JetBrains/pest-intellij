package com.pestphp.pest.inspections

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.inspections.PhpUndefinedFieldInspection
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.pestphp.pest.isAnyPestFunction
import com.pestphp.pest.isThisVariableInPest

class SuppressUndefinedPropertyInspection: InspectionSuppressor {
    companion object {
        private val SUPPRESSED_PHP_INSPECTIONS = listOf(PhpUndefinedFieldInspection().id)
    }

    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (!SUPPRESSED_PHP_INSPECTIONS.contains(toolId)) {
            return false
        }

        val fieldReference = element.parent as? FieldReference ?: return false

        if (!fieldReference.classReference.isThisVariableInPest { it.isAnyPestFunction() }) {
            return false
        }

        return SUPPRESSED_PHP_INSPECTIONS.contains(toolId)
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return SuppressQuickFix.EMPTY_ARRAY
    }
}
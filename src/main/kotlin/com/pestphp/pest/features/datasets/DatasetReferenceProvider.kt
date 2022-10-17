package com.pestphp.pest.features.datasets

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Adds goto and reference support to string literals referring datasets
 */
class DatasetReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<PsiReference> {
        if (element !is StringLiteralExpression) {
            return PsiReference.EMPTY_ARRAY
        }

        val methodReference = element.parent?.parent as? MethodReference ?: return PsiReference.EMPTY_ARRAY

        if (!methodReference.isDatasetCall()) return PsiReference.EMPTY_ARRAY

        return arrayOf(
            DatasetReference(element)
        )
    }
}
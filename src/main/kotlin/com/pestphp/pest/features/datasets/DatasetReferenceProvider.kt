package com.pestphp.pest.features.datasets

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.getRootPhpPsiElements
import com.pestphp.pest.isPestTestReference

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

        val isPestTest = element
            // Parameter list (with call parameters)
            .parent
            // Method reference (pest test call)
            .parent
            // Check if method reference is a pest test
            .isPestTestReference()
        if (!isPestTest) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(
            DatasetReference(element)
        )
    }
}
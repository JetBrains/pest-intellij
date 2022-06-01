package com.pestphp.pest.features.uses

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceHelperRegistrar
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.util.Function
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl

class UsesInDirectoryReferenceProvider: PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val inCall = element.parent.parent as MethodReferenceImpl

        // Check if method call is `in`.
        if (inCall.canonicalText != "in") {
            return PsiReference.EMPTY_ARRAY
        }

        // Check if the function call is `uses`
        val usesCall = inCall.firstPsiChild as? FunctionReferenceImpl ?: return PsiReference.EMPTY_ARRAY
        if (usesCall.canonicalText != "uses") {
            return PsiReference.EMPTY_ARRAY
        }

        val referenceSet = PhpFolderReferenceSet(element, element as StringLiteralExpression, this)

        return referenceSet
            .allReferences
            .toList()
            .toTypedArray()
    }
}
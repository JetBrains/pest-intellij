package com.pestphp.pest.features.configuration

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.pestphp.pest.CONFIGURATION_FUNCTIONS

class ConfigurationInDirectoryReferenceProvider: PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val inCall = element.parent.parent as MethodReferenceImpl

        if (inCall.canonicalText != "in") {
            return PsiReference.EMPTY_ARRAY
        }

        val usesCall = getConfigurationFunctionCall(inCall) as? FunctionReferenceImpl ?: return PsiReference.EMPTY_ARRAY
        if (usesCall.canonicalText !in CONFIGURATION_FUNCTIONS) {
            return PsiReference.EMPTY_ARRAY
        }

        val referenceSet = PhpFolderReferenceSet(element, element as StringLiteralExpression, this)

        return referenceSet
            .allReferences
            .toList()
            .toTypedArray()
    }
}

fun getConfigurationFunctionCall(inCall: MethodReference): FunctionReference? {
    val child = inCall.firstPsiChild
    if (child !is MethodReference) {
        if (child is FunctionReference) {
            return child
        }
        return null
    }
    return getConfigurationFunctionCall(child)
}
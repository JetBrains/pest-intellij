package com.pestphp.pest.datasets

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl

/**
 * Used to register all dataset reference provider
 */
class DatasetReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java)
                .withParents(
                    ParameterList::class.java,
                    MethodReferenceImpl::class.java,
                ),
            DatasetReferenceProvider()
        )
    }
}


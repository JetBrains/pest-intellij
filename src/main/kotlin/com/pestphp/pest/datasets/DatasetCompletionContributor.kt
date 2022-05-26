package com.pestphp.pest.datasets

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl

class DatasetCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withParents(
                StringLiteralExpression::class.java,
                ParameterList::class.java,
                MethodReferenceImpl::class.java,
            ),
            DataSetCompletionProvider()
        )
    }
}

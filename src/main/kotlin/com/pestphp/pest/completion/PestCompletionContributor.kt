package com.pestphp.pest.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.FieldReference

class PestCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withElementType(PhpTokenTypes.IDENTIFIER)
                .withParent(FieldReference::class.java),
            InternalMembersCompletionProvider()
        )

        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withElementType(PhpTokenTypes.IDENTIFIER)
                .withParent(FieldReference::class.java),
            ThisFieldsCompletionProvider()
        )
    }
}

@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations.symbols

import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.openapi.util.TextRange
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.pestphp.pest.features.customExpectations.generators.Method

class PestCustomExpectationSymbolDeclaration(private val element: StringLiteralExpression, private val methodDescriptor: Method) : PsiSymbolDeclaration {
    override fun getDeclaringElement() = element

    override fun getRangeInDeclaringElement() = element.textRangeInParent

    override fun getSymbol(): PestCustomExpectationSymbol {
        val rangeInFile = TextRange.from(element.textRange.startOffset + 1, element.contents.length)
        return PestCustomExpectationSymbol(element.contents, element.containingFile, rangeInFile, methodDescriptor)
    }
}

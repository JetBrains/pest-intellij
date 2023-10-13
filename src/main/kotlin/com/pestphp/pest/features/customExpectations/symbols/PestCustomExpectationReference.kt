@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations.symbols

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.pestphp.pest.features.customExpectations.KEY

class PestCustomExpectationReference(private val methodReference: MethodReference) : PsiSymbolReference {
    override fun getElement(): MethodReference = methodReference

    override fun getRangeInElement() = methodReference.rangeInElement

    override fun resolveReference(): Collection<Symbol?> {
        val pestCustomExtensions = mutableListOf<PestCustomExpectationSymbol>()
        methodReference.name?.let {
            val extensionName = methodReference.name!!
            FileBasedIndex.getInstance()
                .processValues(KEY, extensionName, null, { file, value ->
                    methodReference.manager.findFile(file)?.let { psiFile ->
                        val range = TextRange.from(value.first(), extensionName.length)
                        pestCustomExtensions.add(PestCustomExpectationSymbol(extensionName, psiFile, range))
                    }
                    true
                }, GlobalSearchScope.allScope(methodReference.project))
        }
        return pestCustomExtensions
    }
}

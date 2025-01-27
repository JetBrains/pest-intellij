@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations.symbols

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.Usage
import com.intellij.find.usages.api.UsageSearchParameters
import com.intellij.find.usages.api.UsageSearcher
import com.intellij.model.Pointer
import com.intellij.model.search.LeafOccurrence
import com.intellij.model.search.LeafOccurrenceMapper
import com.intellij.model.search.SearchContext
import com.intellij.model.search.SearchService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.util.Query
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.MethodReference

class PestCustomExpectationUsageSearcher : UsageSearcher {
    override fun collectSearchRequests(parameters: UsageSearchParameters): Collection<Query<out Usage>> {
        val targetSymbol = parameters.target as? PestCustomExpectationSymbol ?: return emptyList()
        val symbolPointer: Pointer<PestCustomExpectationSymbol> = targetSymbol.createPointer()
        val usages = SearchService.getInstance()
            .searchWord(parameters.project, targetSymbol.expectationName).caseSensitive(true)
            .inContexts(SearchContext.inCode()).inFilesWithLanguage(PhpLanguage.INSTANCE)
            .inScope(parameters.searchScope)
            .buildQuery(LeafOccurrenceMapper.withPointer(symbolPointer, ::findReferencesToSymbol))
            .mapping { PsiUsage.textUsage(it.element.containingFile, it.element.nameNode!!.textRange) }
        val selfUsage = PestCustomExtensionDeclarationUsageQuery(targetSymbol.declarationUsage())
        return listOf(usages, selfUsage)
    }
}

fun PestCustomExpectationSymbol.declarationUsage() = PestCustomDeclarationUsage(file, rangeInFile)

class PestCustomDeclarationUsage(
    override val file: PsiFile, override val range: TextRange
) : PsiUsage {
    override val declaration: Boolean
        get() = true

    override fun createPointer() = PsiUsage.textUsage(file, range).createPointer()
}

fun findReferencesToSymbol(
    symbol: PestCustomExpectationSymbol,
    leafOccurrence: LeafOccurrence
): Collection<PestCustomExpectationReference> {
    val methodReference = PhpPsiUtil.getParentOfClass(leafOccurrence.start, MethodReference::class.java)
    if (methodReference?.nameNode == null) return emptyList()
    val possibleReference = PestCustomExpectationReference(methodReference)
    return if (possibleReference.resolvesTo(symbol)) listOf(possibleReference) else emptyList()
}

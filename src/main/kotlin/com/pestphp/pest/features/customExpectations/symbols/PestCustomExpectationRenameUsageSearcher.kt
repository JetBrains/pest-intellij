@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations.symbols

import com.intellij.find.usages.api.PsiUsage
import com.intellij.model.Pointer
import com.intellij.model.search.LeafOccurrenceMapper
import com.intellij.model.search.SearchContext
import com.intellij.model.search.SearchService
import com.intellij.openapi.application.runReadAction
import com.intellij.refactoring.rename.api.PsiModifiableRenameUsage
import com.intellij.refactoring.rename.api.RenameUsage
import com.intellij.refactoring.rename.api.RenameUsageSearchParameters
import com.intellij.refactoring.rename.api.RenameUsageSearcher
import com.intellij.util.AbstractQuery
import com.intellij.util.Processor
import com.intellij.util.Query
import com.jetbrains.php.lang.PhpLanguage

private class PestCustomExpectationRenameUsageSearcher : RenameUsageSearcher {
    override fun collectSearchRequests(parameters: RenameUsageSearchParameters): Collection<Query<out RenameUsage>> {
        val targetSymbol = parameters.target as? PestCustomExpectationSymbol ?: return emptyList()
        val symbolPointer: Pointer<PestCustomExpectationSymbol> = targetSymbol.createPointer()
        val usages = SearchService.getInstance()
            .searchWord(parameters.project, targetSymbol.expectationName)
            .caseSensitive(true)
            .inContexts(SearchContext.inCode())
            .inFilesWithLanguage(PhpLanguage.INSTANCE)
            .inScope(parameters.searchScope)
            .buildQuery(LeafOccurrenceMapper.withPointer(symbolPointer, ::findReferencesToSymbol))
        val selfUsage = PestCustomExtensionDeclarationUsageQuery(
            PsiModifiableRenameUsage.defaultPsiModifiableRenameUsage(targetSymbol.declarationUsage())
        )
        return listOf(usages.mapping {
            PsiModifiableRenameUsage.defaultPsiModifiableRenameUsage(
                PsiUsage.textUsage(it.element.containingFile, it.element.nameNode!!.textRange)
            )
        }, selfUsage)
    }
}

internal class PestCustomExtensionDeclarationUsageQuery<T>(private val targetDeclaration: T) : AbstractQuery<T>() {
    override fun processResults(p0: Processor<in T>): Boolean {
        return runReadAction {
            p0.process(targetDeclaration)
        }
    }
}

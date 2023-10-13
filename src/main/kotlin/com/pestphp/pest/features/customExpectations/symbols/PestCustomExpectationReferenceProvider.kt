@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations.symbols

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.features.customExpectations.symbols.PestCustomExpectationReferenceProvider.Companion.PEST_EXPECTATION

val PEST_EXPECTATION_TYPE: PhpType = PhpType.from(PEST_EXPECTATION)

class PestCustomExpectationReferenceProvider : PsiSymbolReferenceProvider {
    companion object {
        const val PEST_EXPECTATION: String = "\\Pest\\Expectation"
    }

    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        if (element is MethodReferenceImpl) {
            val classReference = element.classReference
            val methodName = element.name
            if (methodName != null && classReference != null && "extend" != methodName &&
                PhpType.intersectsGlobal(element.project, PEST_EXPECTATION_TYPE, classReference.globalType)
            ) {
                // workaround till `com.intellij.lang.javascript.navigation.JSGotoDeclarationHandler#getGotoDeclarationTargets` is not fixed
                if (element.multiResolve(false).isEmpty()) {
                    return arrayListOf(PestCustomExpectationReference(element))
                }
            }
        }
        return emptyList()
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> {
        return (target as? PestCustomExpectationSymbol)?.let { listOf(SearchRequest.of(target.expectationName)) }
            ?: emptyList()
    }
}

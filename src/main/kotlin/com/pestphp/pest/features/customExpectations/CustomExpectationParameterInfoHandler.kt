@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations

import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.ParameterInfoHandlerWithTabActionSupport
import com.intellij.lang.parameterInfo.ParameterInfoUIContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.model.psi.PsiSymbolReferenceService
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.util.IntPair
import com.intellij.util.containers.ContainerUtil
import com.jetbrains.php.lang.PhpParameterInfoHandler
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.pestphp.pest.features.customExpectations.generators.Parameter
import com.pestphp.pest.features.customExpectations.symbols.PestCustomExpectationReference
import com.pestphp.pest.features.customExpectations.symbols.PestCustomExpectationSymbol

class CustomExpectationParameterInfoHandler : ParameterInfoHandlerWithTabActionSupport<FunctionReference, List<Parameter>, PsiElement> {
    override fun findElementForParameterInfo(context: CreateParameterInfoContext): FunctionReference? {
        val methodReference =
            PhpParameterInfoHandler.findAnchorForParameterInfo(context) as? MethodReferenceImpl ?: return null
        val references = PsiSymbolReferenceService.getService().getReferences(methodReference)
        val symbol = references.filterIsInstance<PestCustomExpectationReference>().flatMap { it.resolveReference() }
            .filterIsInstance<PestCustomExpectationSymbol>().firstOrNull() ?: return null
        context.itemsToShow = arrayOf(symbol.methodDescriptor.parameters)
        return methodReference
    }

    override fun showParameterInfo(element: FunctionReference, context: CreateParameterInfoContext) {
        context.showHint(element, element.textRange.startOffset, this)
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): FunctionReference? {
        return PhpParameterInfoHandler.findAnchorForParameterInfo(context) as? FunctionReference
    }

    override fun updateParameterInfo(reference: FunctionReference, context: UpdateParameterInfoContext) {
        context.setCurrentParameter(
            PhpParameterInfoHandler.getCurrentParameterIndex(
                reference, PhpParameterInfoHandler.getCurrentOffset(context),
                actualParameterDelimiterType
            )
        )
    }

    override fun updateUI(params: List<Parameter>, context: ParameterInfoUIContext) {
        if (params.isEmpty()) {
            context.isUIComponentEnabled = false
            return
        }
        var currentParameter = context.currentParameterIndex
        if (currentParameter < 0) currentParameter = 0
        val buffer = StringBuilder()
        val highlightRange =
            PhpParameterInfoHandler.appendParameterInfo(context, buffer, IntPair(-1, -1), currentParameter, params) { p ->
                if (p.returnType.isEmpty) {
                    p.name
                }
                else {
                    "${p.name}: ${p.returnType}"
                }
            }
        context.setupUIComponentPresentation(
            buffer.toString(),
            highlightRange.first,
            highlightRange.second,
            false,
            false,
            false,
            context.defaultParameterColor
        )
    }

    override fun getActualParameters(o: FunctionReference): Array<PsiElement> {
        val parameters = o.parameters
        return parameters.copyOf()
    }

    override fun getActualParameterDelimiterType(): IElementType {
        return PhpTokenTypes.opCOMMA
    }

    override fun getActualParametersRBraceType(): IElementType {
        return PhpTokenTypes.chRPAREN
    }

    override fun getArgumentListAllowedParentClasses(): Set<Class<*>> {
        return ContainerUtil.newHashSet<Class<*>>(PhpPsiElement::class.java)
    }

    override fun getArgListStopSearchClasses(): Set<Class<*>> {
        return ContainerUtil.newHashSet(
            Statement::class.java
        )
    }

    override fun getArgumentListClass(): Class<FunctionReference> {
        return FunctionReference::class.java
    }
}

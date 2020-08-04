package com.pestphp.pest.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.getAllBeforeThisAssignments
import com.pestphp.pest.isPestAfterFunction
import com.pestphp.pest.isPestTestFunction
import com.pestphp.pest.isThisVariableInPest

class ThisFieldsCompletionContributor : CompletionContributor(), GotoDeclarationHandler {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withElementType(PhpTokenTypes.IDENTIFIER).withParent(FieldReference::class.java),
            provider
        )
    }

    override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement> {
        if (sourceElement?.elementType != PhpTokenTypes.IDENTIFIER) return PsiElement.EMPTY_ARRAY

        val fieldReference = sourceElement?.parent as? FieldReference ?: return PsiElement.EMPTY_ARRAY

        if (fieldReference.classReference?.isThisVariableInPest { check(it) } != true) return PsiElement.EMPTY_ARRAY

        return (fieldReference.containingFile ?: return PsiElement.EMPTY_ARRAY).getAllBeforeThisAssignments()
            .filter { it.variable?.name == fieldReference.name }
            .mapNotNull { it.variable }
            .toTypedArray()
    }

    companion object {
        private val provider = Provider()

        private fun check(it: FunctionReferenceImpl) = it.isPestTestFunction() || it.isPestAfterFunction()
    }

    private class Provider : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
            val fieldReference = parameters.originalPosition?.parent as? FieldReference ?: return

            if ((fieldReference.classReference as? Variable)?.name != "this") return

            FileBasedIndex.getInstance().getFileData(
                ThisFieldsIndex.KEY,
                fieldReference.containingFile.virtualFile,
                fieldReference.project
            )
                .keys
                .forEach {
                    result.addElement(
                        LookupElementBuilder.create(it).withIcon(PhpIcons.FIELD)
                    )
                }
        }
    }
}
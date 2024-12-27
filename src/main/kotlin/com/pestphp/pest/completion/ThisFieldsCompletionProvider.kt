package com.pestphp.pest.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.Variable
import com.pestphp.pest.getAllBeforeThisAssignments
import com.pestphp.pest.isAnyPestFunction
import com.pestphp.pest.isThisVariableInPest

/**
 * Adds completion for variable assignments from `beforeEach` when using `$this`
 * inside a pest test.
 */
internal class ThisFieldsCompletionProvider : CompletionProvider<CompletionParameters>(), GotoDeclarationHandler {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val fieldReference = parameters.position.parent as? FieldReference ?: return

        val variable = fieldReference.classReference as? Variable ?: return

        if (!variable.isThisVariableInPest { it.isAnyPestFunction() }) return

        return (fieldReference.containingFile).getAllBeforeThisAssignments()
            .filter { it.variable?.name !== null }
            .forEach {
                result.addElement(
                    LookupElementBuilder.create(it.variable!!.name!!)
                        .withIcon(PhpIcons.FIELD)
                        .withTypeText(it.type.toStringRelativized("\\"))
                )
            }
    }

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement> {
        if (sourceElement?.elementType != PhpTokenTypes.IDENTIFIER) {
            return PsiElement.EMPTY_ARRAY
        }

        val fieldReference = sourceElement?.parent as? FieldReference
            ?: return PsiElement.EMPTY_ARRAY

        if (fieldReference.classReference?.isThisVariableInPest { it.isAnyPestFunction() } != true) {
            return PsiElement.EMPTY_ARRAY
        }

        return (fieldReference.containingFile ?: return PsiElement.EMPTY_ARRAY).getAllBeforeThisAssignments()
            .filter { it.variable?.name == fieldReference.name }
            .mapNotNull { it.variable }
            .toTypedArray()
    }
}

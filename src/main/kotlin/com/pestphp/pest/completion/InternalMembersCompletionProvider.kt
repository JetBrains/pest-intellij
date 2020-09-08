package com.pestphp.pest.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.completion.PhpVariantsUtil
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.Variable
import com.pestphp.pest.isAnyPestFunction
import com.pestphp.pest.isThisVariableInPest

class InternalMembersCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val fieldReference = parameters.position.parent as? FieldReference ?: return

        val variable = fieldReference.classReference as? Variable ?: return

        if (!variable.isThisVariableInPest { it.isAnyPestFunction() }) return

        val phpIndex = PhpIndex.getInstance(fieldReference.project)
        val classes = phpIndex.completeType(fieldReference.project, variable.type, null).types
            .filter { it.startsWith("\\") }
            .flatMap {
                phpIndex.getAnyByFQN(it)
            }

        classes.flatMap { phpClass ->
            phpClass.methods.filter { it.access.isProtected || (!it.access.isPrivate && it.isStatic) }
        }.forEach {
            result.addElement(PhpVariantsUtil.getLookupItem(it, null))
        }

        classes.flatMap { phpClass ->
            phpClass.fields.filter { it.modifier.isProtected }
        }.forEach {
            result.addElement(PhpVariantsUtil.getLookupItem(it, null))
        }
    }
}

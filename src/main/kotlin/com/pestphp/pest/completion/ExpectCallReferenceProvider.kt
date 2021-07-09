package com.pestphp.pest.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpPresentationUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.impl.FieldReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.pestphp.pest.PestIcons
import com.pestphp.pest.expectationType
import com.pestphp.pest.indexers.ExpectExtendIndex

class ExpectCallReferenceProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val methodReference = parameters.position.parent as? FieldReferenceImpl ?: return

        val classReference = methodReference.classReference
        // Class reference should be a method or function call or field reference (and method)
        if (classReference !is FunctionReference && classReference !is FieldReferenceImpl) return

        val type = classReference.type

        if (type.types.none { it.contains("expect") }) return

        val lookupElements = FileBasedIndex.getInstance().getAllKeys(
            ExpectExtendIndex.key,
            methodReference.project
        ).map {
            LookupElementBuilder.create(it)
                .withIcon(PestIcons.LOGO)
                .withTypeText(expectationType.toStringResolved().removePrefix("\\"))
                .withInsertHandler { context, _ -> run {
                        val editor = context.editor
                        val caretModel = editor.caretModel
                        val document = editor.document

                        document.insertString(caretModel.offset, "()")
                        caretModel.moveCaretRelatively(
                            1,
                            0,
                            false,
                            false,
                            false
                        )
                    }
                }
                .withTailText(
                    "()",
                    true
                )
        }

        result.addAllElements(lookupElements)
    }
}

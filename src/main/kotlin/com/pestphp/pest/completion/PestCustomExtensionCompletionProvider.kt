@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.completion.PhpCompletionUtil
import com.jetbrains.php.completion.insert.PhpInsertHandlerUtil
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.features.customExpectations.KEY
import com.pestphp.pest.features.customExpectations.expectationType
import com.pestphp.pest.features.customExpectations.toMethod

class PestCustomExtensionCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val memberReference = parameters.position.originalElement.parent as? MemberReference ?: return
        val project = parameters.position.project
        if (PhpType.intersectsGlobal(project, expectationType, memberReference.classReference?.globalType ?: PhpType.EMPTY)) {
            val index = FileBasedIndex.getInstance()
            index.getAllKeys(KEY, project).forEach { extensionName ->
                index.processValues(KEY, extensionName, null, { file, value ->
                    memberReference.manager.findFile(file)?.let { psiFile ->
                        val cheapRenderer = CustomExpectationRenderer()
                        val lookupElement = LookupElementBuilder.create(extensionName)
                            .withRenderer(cheapRenderer)
                            .withExpensiveRenderer(CustomExtensionExpensiveRenderer(cheapRenderer, psiFile, value.first()))
                            .withInsertHandler { context, _ ->
                                val expectation = psiFile.findElementAt(value.first())
                                val methodDescriptor =
                                    PhpPsiUtil.getParentOfClass(expectation, MethodReference::class.java)?.toMethod()
                                PhpInsertHandlerUtil.insertStringAtCaret(context.editor, "()")
                                if (methodDescriptor?.parameters?.isNotEmpty() == true) {
                                    PhpCompletionUtil.moveCaretRelativelyWithScroll(context.editor, -1)
                                    AutoPopupController.getInstance(project).autoPopupParameterInfo(context.editor, null)
                                }
                            }
                        result.addElement(lookupElement)
                    }
                    true
                }, GlobalSearchScope.projectScope(project))
            }
        }
    }
}

private class CustomExpectationRenderer : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, presentation: LookupElementPresentation) {
        presentation.icon = PhpIcons.METHOD
        presentation.itemText = element.lookupString
        presentation.isItemTextBold = true
    }
}

private class CustomExtensionExpensiveRenderer(
    private val cheapRenderer: CustomExpectationRenderer,
    private val targetFile: PsiFile,
    private val expectationOffset: Int
) : LookupElementRenderer<LookupElement>() {
    override fun renderElement(element: LookupElement, presentation: LookupElementPresentation) {
        cheapRenderer.renderElement(element, presentation)
        val expectation = targetFile.findElementAt(expectationOffset)
        PhpPsiUtil.getParentOfClass(expectation, MethodReference::class.java)?.toMethod()?.let {
            val params = it.parameters.map { p ->
                if (p.returnType.isEmpty) {
                    p.name
                } else {
                    "${p.name}: ${p.returnType}"
                }
            }
            presentation.tailText = "(${params.joinToString(", ")})"
            presentation.typeText = it.returnType.global(targetFile.project).toString()
        }
    }
}

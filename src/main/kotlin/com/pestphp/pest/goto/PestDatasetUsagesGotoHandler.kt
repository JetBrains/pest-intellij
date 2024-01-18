package com.pestphp.pest.goto

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.features.datasets.isDatasetCall
import com.pestphp.pest.features.datasets.isPestDatasetFunction

fun getDatasetUsages(literal: StringLiteralExpression): Array<PsiElement>? {
    val function = literal.parent?.parent as? FunctionReferenceImpl ?: return null

    if (!function.isPestDatasetFunction()) return null

    val searchHelper = PsiSearchHelper.getInstance(literal.project)
    val result = mutableListOf<PsiElement>()
    val datasetName = literal.contents

    val processor = Processor { psiFile: PsiFile ->
        result.addAll(
            PsiTreeUtil.findChildrenOfType(psiFile, StringLiteralExpression::class.java).filter {
                it.contents == datasetName
            }.mapNotNull {
                it.parent?.parent as? MethodReference
            }.filter {
                it.isDatasetCall()
            }
        )

        true
    }

    searchHelper.processAllFilesWithWordInLiterals(
        datasetName,
        GlobalSearchScope.allScope(literal.project),
        processor,
    )

    return result.toTypedArray()
}

class PestDatasetUsagesGotoHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {
        val literal = sourceElement?.parent as? StringLiteralExpression ?: return null
        return getDatasetUsages(literal)
    }
}

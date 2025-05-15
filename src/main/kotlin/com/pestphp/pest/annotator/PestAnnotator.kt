package com.pestphp.pest.annotator

import com.intellij.codeInsight.daemon.impl.HighlightRangeExtension
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.resolve.types.PhpParameterBasedTypeProvider

class PestAnnotator: Annotator, HighlightRangeExtension {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (PhpParameterBasedTypeProvider.isMeta(holder.currentAnnotationSession.file)) return
        element.accept(PestAnnotatorVisitor(holder))
    }

    override fun isForceHighlightParents(psiFile: PsiFile): Boolean {
        return false
    }
}
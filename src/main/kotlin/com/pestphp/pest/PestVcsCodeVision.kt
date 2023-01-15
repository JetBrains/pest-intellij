package com.pestphp.pest

import com.intellij.codeInsight.hints.VcsCodeVisionLanguageContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import java.awt.event.MouseEvent

/**
 * Adds support for showing author on pest tests.
 */
class PestVcsCodeVision: VcsCodeVisionLanguageContext {
    override fun handleClick(mouseEvent: MouseEvent, editor: Editor, element: PsiElement) {
        // Do nothing, rely on default
    }

    override fun isAccepted(element: PsiElement): Boolean {
        if (element !is FunctionReferenceImpl) {
            return false
        }

        if (!element.isAnyPestFunction()) {
            return false
        }

        return true
    }
}
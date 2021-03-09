package com.pestphp.pest

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

class PestIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (DumbService.isDumb(element.project)) return null

        if (element !is PsiFile) return null

        if (!element.isPestTestFile()) return null

        return PestIcons.FILE
    }
}

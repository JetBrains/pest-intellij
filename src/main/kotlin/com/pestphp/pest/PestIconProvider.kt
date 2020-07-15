package com.pestphp.pest

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

class PestIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        return (element as? PsiFile)
                ?.isPestTestFile()
                .let { PestIcons.FILE }
    }
}
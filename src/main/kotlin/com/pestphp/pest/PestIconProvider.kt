package com.pestphp.pest

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.pestphp.pest.features.datasets.isIndexedPestDatasetFile
import javax.swing.Icon

class PestIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (DumbService.isDumb(element.project)) return null

        if (element !is PsiFile) return null

        return findIconFromPsiFile(element)
    }

    private fun findIconFromPsiFile(file: PsiFile): Icon? {
        if (file.isIndexedPestTestFile()) {
            return PestIcons.File
        }

        if (file.isIndexedPestDatasetFile()) {
            return PestIcons.Dataset
        }

        if (file.isPestFile()) {
            return PestIcons.Logo
        }

        return null
    }
}

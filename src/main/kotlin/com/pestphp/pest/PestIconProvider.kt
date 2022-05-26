package com.pestphp.pest

import com.intellij.ide.IconProvider
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.pestphp.pest.datasets.isPestDatasetFile
import javax.swing.Icon

class PestIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (DumbService.isDumb(element.project)) return null

        if (element !is PsiFile) return null

        return runReadAction { findIconFromPsiFile(element) }
    }

    private fun findIconFromPsiFile(file: PsiFile): Icon?
    {
        if (file.isPestTestFile()) {
            return PestIcons.FILE
        }

        if (file.isPestDatasetFile()) {
            return PestIcons.DATASET_FILE
        }

        if (file.isPestFile()) {
            return PestIcons.LOGO
        }

        return null
    }
}

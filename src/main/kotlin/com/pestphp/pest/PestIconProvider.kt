package com.pestphp.pest

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.Icon

class PestIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (DumbService.isDumb(element.project)) return null

        if (element !is PsiFile) return null

        if (element.isPestTestFile()) {
            return PestIcons.FILE
        }

        if (element.isPestDatasetFile()) {
            return PestIcons.DATASET_FILE
        }

        val projectDir = element.project.guessProjectDir() ?: return null
        val pestFilePath = PestSettings.getInstance(element.project).pestFilePath
        if (element.virtualFile.path == projectDir.path + "/" + pestFilePath) {
            return PestIcons.LOGO
        }

        return null
    }
}

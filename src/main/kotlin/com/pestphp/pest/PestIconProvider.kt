package com.pestphp.pest

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.features.datasets.isIndexedPestDatasetFile
import javax.swing.Icon

class PestIconProvider : FileIconProvider {
    override fun getIcon(vFile: VirtualFile, flags: Int, project: Project?): Icon? {
        if (project == null || DumbService.isDumb(project)) return null
        val file = PsiManager.getInstance(project).findFile(vFile) as? PhpFile ?: return null
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

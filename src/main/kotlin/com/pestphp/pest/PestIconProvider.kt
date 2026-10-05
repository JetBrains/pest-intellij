package com.pestphp.pest

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.ProjectScope
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.features.datasets.isIndexedPestDatasetFile
import com.pestphp.pest.features.datasets.isPestDatasetFileCandidate
import com.pestphp.pest.indexers.isPestTestFileCandidate
import javax.swing.Icon

class PestIconProvider : FileIconProvider {
    override fun getIcon(vFile: VirtualFile, flags: Int, project: Project?): Icon? {
        if (project == null || DumbService.isDumb(project)) return null
        val file = PsiManager.getInstance(project).findFile(vFile) as? PhpFile ?: return null
        val isInProject = ProjectScope.getProjectScope(project).contains(vFile)
        if (isInProject && vFile.isPestTestFileCandidate() && file.isIndexedPestTestFile()) {
            return PestIcons.File
        }
        if (isInProject && vFile.isPestDatasetFileCandidate() && file.isIndexedPestDatasetFile()) {
           return PestIcons.Dataset
        }
        if (file.isPestFile()) {
            return PestIcons.Logo
        }
        return null
    }
}

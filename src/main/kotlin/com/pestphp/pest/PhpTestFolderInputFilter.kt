package com.pestphp.pest

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.jetbrains.php.lang.PhpFileType

open class PhpTestFolderInputFilter : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
    override fun acceptInput(file: VirtualFile): Boolean {
        if (file.path.contains(""".*?test.*?/.*\..*""".toRegex())) {
            return true
        }

        return ProjectManager.getInstance().openProjects.any {
            TestSourcesFilter.isTestSources(file, it)
        }
    }
}
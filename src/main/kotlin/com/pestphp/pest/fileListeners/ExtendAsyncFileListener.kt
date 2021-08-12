package com.pestphp.pest.fileListeners

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.autoimport.AsyncFileChangeListenerBase
import com.intellij.openapi.project.NoAccessDuringPsiEvents
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiManager
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.expectExtends
import com.pestphp.pest.isPestEnabled
import com.pestphp.pest.services.ExpectationFileService

class ExtendAsyncFileListener : AsyncFileChangeListenerBase() {
    override fun apply() {
        // No implementation needed
    }

    override fun init() {
        // No implementation needed
    }

    override fun updateFile(file: VirtualFile, event: VFileEvent) {
        ProjectManager.getInstance()
            .openProjects
            // Only look at projects where the file is inside.
            .filter { ProjectFileIndex.getInstance(it).isInContent(file) }
            // Get the PSI file inside each of the projects.
            .mapNotNull { PsiManager.getInstance(it).findFile(file) }
            // Only look at PHP files
            .filterIsInstance<PhpFile>()
            .forEach {
                val expectationFileService = it.project.service<ExpectationFileService>()

                // In case file is deleted.
                if (event is VFileDeleteEvent) {
                    expectationFileService.removeExtends(it.virtualFile)
                    return@forEach
                }

                invokeLater {
                    // Add all the extends
                    val hasChanges = expectationFileService.updateExtends(it)

                    if (hasChanges) {
                        // Generate the file if any changes
                        expectationFileService.generateFile()
                    }
                }
            }
    }

    override fun isRelevant(file: VirtualFile, event: VFileEvent): Boolean {
        // Check if any of the projects has the file
        return ProjectManager.getInstance()
            .openProjects
            .any {
                it.isPestEnabled() && ProjectFileIndex.getInstance(it).isInContent(file)
            }
    }

    override fun isRelevant(path: String): Boolean {
        return PhpFileType.INSTANCE.extensions.any {
            FileUtilRt.extensionEquals(path, it)
        }
    }
}

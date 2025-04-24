package com.pestphp.pest.features.customExpectations

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.php.composer.ComposerConfigListener
import com.jetbrains.php.composer.ComposerDataService
import com.jetbrains.php.composer.lib.ComposerLibraryServiceFactory

class CustomExpectationRemoveGeneratedFileStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        StartupManager.getInstance(project).runWhenProjectIsInitialized {
            tryDeleteGeneratedExpectationFile(project)
        }
        ComposerDataService.getInstance(project).addConfigListener(object : ComposerConfigListener {
            override fun configPathChanged(oldPath: String?, newPath: String?, isWellConfigured: Boolean) {
                if (newPath != null) {
                    tryDeleteGeneratedExpectationFile(project)
                }
            }
        })
    }

    private fun tryDeleteGeneratedExpectationFile(project: Project) {
        ComposerLibraryServiceFactory.getInstance(project, null as VirtualFile?).vendorDir?.findChild("Expectation.php")?.let {
            runWriteAction { it.delete(null) }
        }
    }
}

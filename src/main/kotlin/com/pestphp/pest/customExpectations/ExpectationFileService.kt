package com.pestphp.pest.customExpectations

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.jetbrains.php.composer.lib.ComposerLibraryManager
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.rd.util.first
import com.pestphp.pest.customExpectations.generators.ExpectationGenerator
import com.pestphp.pest.customExpectations.generators.Method
import com.pestphp.pest.realPath

class ExpectationFileService(val project: Project) {
    private var methods: Map<String, List<Method>> = mutableMapOf()

    /**
     * Update te extends coming from a specific file.
     * If nothing was updated, false will be returned, if changes were made,
     * then true would be returned.
     */
    fun updateExtends(file: PhpFile, customExpectations: List<Method>): Boolean {
        val fileName = file.realPath

        val methods = this.methods.toMutableMap()
        val beforeMethods = methods[fileName]

        val newMethods = mapOf(
            fileName to customExpectations
        )

        // If no methods were registered before and no methods were found, skip.
        if (beforeMethods === null && newMethods.first().value.isEmpty()) {
            return false
        }

        this.methods = methods.plus(newMethods)
        return beforeMethods != newMethods
    }

    fun generateFile(afterGenerationRunnable: () -> Unit) {
        val generator = ExpectationGenerator()

        // Add all methods to the generator
        methods.values
            .flatten()
            .distinct()
            .let { generator.docMethods.addAll(it) }

        // Save the file in vendor folder
        DumbService.getInstance(project).smartInvokeLater {
            // Generate the file
            val newFile = runReadAction { generator.generateToFile(project) }

            runWriteAction {
                DumbService.getInstance(project).suspendIndexingAndRun(
                    "Indexing Pest expect extends"
                ) {
                    // Get the composer directory
                    val composer =
                        ComposerLibraryManager.getInstance(project).findVendorDirForUpsource()
                            ?: return@suspendIndexingAndRun
                    val directory =
                        PsiManager.getInstance(project).findDirectory(composer) ?: return@suspendIndexingAndRun

                    // Check if file already exist and delete it so we can make it again.
                    val oldExpectationFile = directory.findFile(newFile.viewProvider.virtualFile.name)

                    if (oldExpectationFile === null) {
                        directory.add(newFile)
                        return@suspendIndexingAndRun
                    }

                    runUndoTransparentWriteAction {
                        oldExpectationFile.firstChild.replace(newFile.firstChild)
                        afterGenerationRunnable()
                    }
                }
            }
        }
    }
}

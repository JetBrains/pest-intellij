package com.pestphp.pest.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.jetbrains.php.composer.lib.ComposerLibraryManager
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.pestphp.pest.expectExtends
import com.pestphp.pest.extendName
import com.pestphp.pest.generators.ExpectationGenerator

class ExpectationFileService(val project: Project) {
    private val methods: MutableMap<String, List<ExpectationGenerator.Method>> = mutableMapOf()

    /**
     * Update te extends coming from a specific file.
     * If nothing was updated, false will be returned, if changes were made,
     * then true would be returned.
     */
    fun updateExtends(phpFile: PhpFile): Boolean {
        val beforeMethods = methods[phpFile.virtualFile.path]

        val newMethods = phpFile.expectExtends
            .filter { it.extendName !== null }
            .mapNotNull {
                val extendName = it.extendName ?: return@mapNotNull null

                val closure = (it.parameters[1] as? PhpExpression)?.firstChild as? Function

                if (closure === null) {
                    return@mapNotNull null
                }

                ExpectationGenerator.Method(
                    extendName,
                    closure.globalType,
                    closure.parameters.asList()
                )
            }
            .also {
                methods[phpFile.virtualFile.path] = it.toMutableList()
            }

        if (beforeMethods === null && newMethods.isEmpty()) {
            return false
        }


        return beforeMethods != newMethods
    }

    fun generateFile() {
        val generator = ExpectationGenerator()

        // Add all methods to the generator
        methods.values
            .flatten()
            .let { generator.docMethods.addAll(it) }

        // Generate the file
        val file = generator.generateToFile(project)

        // Save the file in vendor folder
        ApplicationManager.getApplication().invokeLater {
            ApplicationManager.getApplication().runWriteAction {
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
                    val expectationFile = directory.findFile(file.viewProvider.virtualFile.name)
                    expectationFile?.delete()

                    directory.add(file)
                }
            }
        }
    }
}
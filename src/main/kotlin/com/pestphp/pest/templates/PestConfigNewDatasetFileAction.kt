package com.pestphp.pest.templates

import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestIcons


/**
 * Shows the "Create Pest Dataset File" action in the context menu when creating a new file.
 *
 * This will only show if the file is being created in a directory named "tests".
 */
class PestConfigNewDatasetFileAction : PestConfigNewFileAction() {
    companion object {
        const val PEST_SHARED_DATASET_TEMPLATE = "Pest Shared Dataset"
        const val PEST_SCOPED_DATASET_TEMPLATE = "Pest Scoped Dataset"
    }
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle(PestBundle.message("CREATE_NEW_PEST_DATASET_DIALOG_TITLE"))
            .addKind(PestBundle.message("CREATE_NEW_PEST_SHARED_DATASET"), PestIcons.Dataset, PEST_SHARED_DATASET_TEMPLATE)
            .addKind(PestBundle.message("CREATE_NEW_PEST_SCOPED_DATASET"), PestIcons.Dataset, PEST_SCOPED_DATASET_TEMPLATE)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return PestBundle.message("ACTIONS_NEW_DATASET_ACTION_NAME")
    }

    override fun createFileFromTemplate(name: String?, template: FileTemplate, dir: PsiDirectory): PsiFile {
        if (template.name == PEST_SHARED_DATASET_TEMPLATE) {
            // find parent directory named "tests"
            var parentDir = dir
            while (parentDir.name != "tests") {
                parentDir = parentDir.parentDirectory ?: break
            }

            val datasetDir = parentDir.findSubdirectory("Datasets")
                ?: parentDir.createSubdirectory("Datasets")

            // Check if first character is lowercase in name
            var newName = name
            if (name!![0].isLowerCase()) {
                newName = name.replaceFirstChar { it.uppercase() }
            }

            return createFileFromTemplate(
                newName,
                template,
                datasetDir,
                defaultTemplateProperty,
                true,
                mapOf("DATASET_NAME" to name.replaceFirstChar { it.lowercase() })
            )!!
        }

        return createFileFromTemplate(
            "Datasets",
            template,
            dir,
            defaultTemplateProperty,
            true,
            mapOf("DATASET_NAME" to name!!.replaceFirstChar { it.lowercase() })
        )!!
    }
}

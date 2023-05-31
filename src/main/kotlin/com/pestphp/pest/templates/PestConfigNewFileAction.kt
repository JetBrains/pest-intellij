package com.pestphp.pest.templates

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.pestphp.pest.PestIcons


class PestConfigNewFileAction :
    CreateFileFromTemplateAction() {

    override fun isAvailable(dataContext: DataContext): Boolean {
        val view = LangDataKeys.IDE_VIEW.getData(dataContext)
        var psiDir: PsiDirectory? = null
        if (view != null) {
            val directories = view.directories
            if (directories.size == 1) {
                psiDir = directories[0]
            }
        }

        if (psiDir == null || !psiDir.isValid) {
            return false
        }

        val virtualDir = psiDir.virtualFile
        if (!virtualDir.isValid || !virtualDir.isDirectory) {
            return false
        }

        return virtualDir.path.contains("tests")
    }

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("Create Pest Test File")
            .addKind("It", PestIcons.FILE, "Pest It")
            .addKind("Test", PestIcons.FILE, "Pest Test")
            .addKind("Shared dataset", PestIcons.DATASET_FILE, "Pest Shared Dataset")
            .addKind("Scoped dataset", PestIcons.DATASET_FILE, "Pest Scoped Dataset")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Pest Test"
    }

    override fun createFileFromTemplate(name: String?, template: FileTemplate, dir: PsiDirectory): PsiFile {
        if (template.name == "Pest Shared Dataset") {
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

        if (template.name == "Pest Scoped Dataset") {
            return createFileFromTemplate(
                "Datasets",
                template,
                dir,
                defaultTemplateProperty,
                true,
                mapOf("DATASET_NAME" to name!!.replaceFirstChar { it.lowercase() })
            )!!
        }

        var testName = name
        if (!name!!.endsWith("test", true)) {
            testName = "${name}Test"
        }

        return super.createFileFromTemplate(testName, template, dir)
    }
}

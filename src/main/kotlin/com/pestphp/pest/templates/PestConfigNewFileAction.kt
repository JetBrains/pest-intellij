package com.pestphp.pest.templates

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestIcons
import com.pestphp.pest.PestSettings
import com.pestphp.pest.PestSettings.TestFlavor


/**
 * Shows the "Create Pest Test File" action in the context menu when creating a new file.
 *
 * This will only show if the file is being created in a directory named "tests".
 */
open class PestConfigNewFileAction : CreateFileFromTemplateAction() {
    companion object {
        const val PEST_IT_TEMPLATE = "Pest It"
        const val PEST_TEST_TEMPLATE = "Pest Test"
    }

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
            .setTitle(PestBundle.message("CREATE_NEW_PEST_TEST_DIALOG_TITLE"))
            .addKind(PestBundle.message("CREATE_NEW_PEST_IT_FLAVOR"), PestIcons.File, PEST_IT_TEMPLATE)
            .addKind(PestBundle.message("CREATE_NEW_PEST_TEST_FLAVOR"), PestIcons.File, PEST_TEST_TEMPLATE)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return PestBundle.message("ACTIONS_NEW_TEST_ACTION_NAME")
    }

    override fun createFileFromTemplate(name: String?, template: FileTemplate, dir: PsiDirectory): PsiFile {
        PestSettings.getInstance(dir.project).preferredTestFlavor = if (template.name == PEST_IT_TEMPLATE) TestFlavor.IT
            else TestFlavor.TEST

        var testName = name
        if (!name!!.endsWith("test", true)) {
            testName = "${name}Test"
        }

        return super.createFileFromTemplate(testName, template, dir)
    }

    override fun getDefaultTemplateName(dir: PsiDirectory): String {
        return if (PestSettings.getInstance(dir.project).preferredTestFlavor == TestFlavor.IT) {
            PEST_IT_TEMPLATE
        } else {
            PEST_TEST_TEMPLATE
        }
    }
}

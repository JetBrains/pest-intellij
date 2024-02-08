package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.testFramework.PhpUnitAbstractTestCreateInfo
import com.pestphp.pest.inspections.convertTestNameToSentenceCase
import javax.swing.Icon

const val INTERNAL_PEST_FILE_TEMPLATE_NAME = "Pest file from class"

object PestTestCreateInfo : PhpUnitAbstractTestCreateInfo() {
    override fun getName(): String {
        return "Pest"
    }

    override fun getTemplateName(): String {
        return INTERNAL_PEST_FILE_TEMPLATE_NAME
    }

    override fun getIcon(): Icon {
        return PestIcons.Logo
    }

    override fun getTestMethodText(project: Project, classFqn: String, methodName: String): String {
        return "test('${convertTestNameToSentenceCase(methodName)}', function(){})"
    }

    override fun shouldPostprocessTemplateFile(): Boolean {
        return true
    }

    override fun postprocessTemplateFile(file: PsiFile) {
        val test = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)
        test?.parent?.delete()
    }
}
package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.util.text.NameUtilCore
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ChangeTestNameCasingQuickFix : LocalQuickFix {
    companion object {
        const val QUICK_FIX_NAME = "Change test name casing to sentence case"
    }

    override fun getFamilyName(): String {
        return QUICK_FIX_NAME
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val nameParameter = descriptor.psiElement as? StringLiteralExpression ?: return
        val pestTestName = nameParameter.contents

        val newNameParameter = PhpPsiElementFactory.createStringLiteralExpression(
          project,
          NameUtilCore.splitNameIntoWords(pestTestName).joinToString(" "),
          true
        )

        nameParameter.replace(newNameParameter)
    }
}
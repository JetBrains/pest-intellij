package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.util.text.NameUtilCore
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.pestphp.pest.PestBundle

class ChangeTestNameCasingQuickFix : LocalQuickFix {
    override fun getFamilyName(): String {
        return PestBundle.message("QUICK_FIX_CHANGE_TEST_NAME_CASING")
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
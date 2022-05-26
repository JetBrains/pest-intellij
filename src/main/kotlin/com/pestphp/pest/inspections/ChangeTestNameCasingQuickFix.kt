package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import net.pearx.kasechange.splitToWords

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
            pestTestName.splitToWords().joinToString(" "),
            true
        )

        nameParameter.replace(newNameParameter)
    }
}
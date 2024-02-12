package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.util.text.NameUtilCore
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.pestphp.pest.PestBundle

fun convertTestNameToSentenceCase(
    name: String,
    shouldLowercaseWords: Boolean = true
) = NameUtilCore.splitNameIntoWords(name).fold("") { acc, element ->
    val word = if (shouldLowercaseWords) element.replaceFirstChar(Char::lowercase) else element
    if (acc.lastOrNull()?.isLetterOrDigit() != true || word.length == 1 && !word[0].isLetterOrDigit())
        "$acc$word"
    else
        "$acc $word"
}

fun isInvalidNameCase(name: String) = !name.contains(' ') && convertTestNameToSentenceCase(name, false) != name

class ChangeTestNameCasingQuickFix : LocalQuickFix {
    override fun getFamilyName(): String {
        return PestBundle.message("QUICK_FIX_CHANGE_TEST_NAME_CASING")
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val nameParameter = descriptor.psiElement as? StringLiteralExpression ?: return
        val pestTestName = nameParameter.contents

        val newNameParameter = PhpPsiElementFactory.createStringLiteralExpression(
            project,
            convertTestNameToSentenceCase(pestTestName),
            true
        )

        nameParameter.replace(newNameParameter)
    }
}
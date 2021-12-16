package com.pestphp.pest.surrounders

import com.intellij.lang.surroundWith.Surrounder
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.PhpPsiElementFactory

class ExpectStatementSurrounder : Surrounder {
    override fun getTemplateDescription(): String {
        return "Expect"
    }

    override fun isApplicable(elements: Array<out PsiElement>): Boolean {
        return true
    }

    override fun surroundElements(project: Project, editor: Editor, elements: Array<out PsiElement>): TextRange? {
        val template = PhpPsiElementFactory.createStatement(
            project,
            "expect(${elements.joinToString("") { it.text }})"
        )

        val lastElement = elements.last()
        val replaced = lastElement.replace(template)
        elements.forEach { it.delete() }

        return replaced.textRange
    }
}

package com.pestphp.pest.templates

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

/**
 * Register postfix templates
 */
class PestPostfixTemplateProvider: PostfixTemplateProvider {
    override fun getTemplates(): MutableSet<PostfixTemplate> {
        return mutableSetOf(PestItPostfixTemplate())
    }

    override fun isTerminalSymbol(currentChar: Char): Boolean {
        return currentChar == '.';
    }

    override fun preExpand(file: PsiFile, editor: Editor) = Unit

    override fun afterExpand(file: PsiFile, editor: Editor) = Unit

    override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile {
        return copyFile
    }
}
package com.pestphp.pest.templates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.pestphp.pest.isPestTestFile

/**
 * Adds a template context to be used in live templates
 *
 * This Pest root template checks if the context is the root of a
 * pest test file.
 */
class PestRootTemplateContextType : TemplateContextType("ROOT_PESTPHP", "Pest root") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        if (!templateActionContext.file.isPestTestFile()) {
            return false
        }

        val element = templateActionContext.file.findElementAt(templateActionContext.startOffset)

        if (element?.parent is StringLiteralExpression) {
            return false
        }

        // Get root element
        val root = element?.parent?.parent?.parent?.parent

        // Check if in root is namespace or file
        return root is PhpFile || root is PhpNamespace
    }
}
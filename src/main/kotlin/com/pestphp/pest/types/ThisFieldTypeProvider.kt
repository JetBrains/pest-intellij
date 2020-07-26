package com.pestphp.pest.types

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.isPestAfterFunction
import com.pestphp.pest.isPestBeforeFunction
import com.pestphp.pest.isPestTestFunction

class ThisFieldTypeProvider : BaseTypeProvider(), PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0222'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        val fieldReference = psiElement as? FieldReference ?: return null

        if (!fieldReference.classReference.isThisVariableInPest { it.isPestTestFunction() || it.isPestAfterFunction() }) return null

        val fieldName = fieldReference.name ?: return null

        return psiElement.containingFile.firstChild.children
            .filterIsInstance<Statement>()
            .mapNotNull { it.firstChild }
            .filterIsInstance<FunctionReferenceImpl>()
            .filter { it.isPestBeforeFunction() }
            .mapNotNull { it.parameterList?.getParameter(0) }
            .flatMap { PsiTreeUtil.findChildrenOfType(it, AssignmentExpression::class.java) }
            .filter { isNeededFieldReference(it.variable, fieldName) }
            .mapNotNull { it.value }
            .filterIsInstance<PhpTypedElement>()
            .firstOrNull()?.type
    }

    private fun isNeededFieldReference(psiElement: PsiElement?, fieldName: String): Boolean {
        if (psiElement !is FieldReference) return false

        if (psiElement.name != fieldName) return false

        if ((psiElement.classReference as? Variable)?.name != "this") return false

        return true
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }
}

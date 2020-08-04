package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.getAllBeforeThisAssignments
import com.pestphp.pest.isPestAfterFunction
import com.pestphp.pest.isPestTestFunction
import com.pestphp.pest.isThisVariableInPest

class ThisFieldTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0222'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (DumbService.isDumb(psiElement.project)) return null

        val fieldReference = psiElement as? FieldReference ?: return null

        if (!fieldReference.classReference.isThisVariableInPest { check(it) }) return null

        val fieldName = fieldReference.name ?: return null

        return (psiElement.containingFile ?: return null).getAllBeforeThisAssignments()
            .filter { (it.variable as? FieldReference)?.name ==  fieldName }
            .mapNotNull { it.value }
            .filterIsInstance<PhpTypedElement>()
            .firstOrNull()?.type
    }

    private fun check(it: FunctionReferenceImpl) = it.isPestTestFunction() || it.isPestAfterFunction()

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

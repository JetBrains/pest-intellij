package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.isExpectation

class ExpectExtendTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0223'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (DumbService.isDumb(psiElement.project)) return null

        // Check that our current element is a method reference
        // expect($data)->toCustomExpect()
        if (psiElement !is MethodReferenceImpl) {
            return null
        }

        val classReference = psiElement.classReference
        // Class reference should be a method or function call
        if (classReference !is FunctionReference) return null

        val type = classReference.type

        if (type.types.none { it.contains("expect") }) {
            return null
        }

        return PhpType().add("#${this.key}${classReference.signature}")
    }

    override fun complete(expression: String, project: Project): PhpType? {
        // Remove key from signature
        val signature = expression.removePrefix("#${key}")

        // Resolve type of the signature.
        val type = PhpIndex.getInstance(project)
            .getBySignature(signature.split('|')[0])
            .map { it.type }
            .reduce { acc, phpType -> acc.add(phpType) }
            .global(project)

        if (! type.isExpectation(project)) {
            return null
        }

        return type
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }
}

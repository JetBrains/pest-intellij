package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.features.customExpectations.expectationType


class HigherOrderExtendTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0224'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (DumbService.isDumb(psiElement.project)) return null

        val reference = psiElement as? MemberReference ?: return null

        if (reference !is FieldReference && reference !is MethodReference) return null

        val expectCall = getExpectCall(reference) ?: return null

        if (expectCall.parameters.isEmpty()) return null

        val firstParameterType = (expectCall.parameters[0] as? PhpTypedElement)?.type ?: return null

        return PhpType().add(firstParameterType).add(expectationType)
    }

    private fun getExpectCall(reference: MemberReference, depth: Int = 100): FunctionReferenceImpl? {
        if (depth <= 0) return null

        return when (val classReference = reference.classReference) {
            is FunctionReferenceImpl -> if (classReference.name == "expect") classReference else null
            is MemberReference -> getExpectCall(classReference, depth - 1)
            else -> null
        }
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }
}

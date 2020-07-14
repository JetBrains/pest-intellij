package com.pestphp.pest.types

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4

class ThisTypeProvider: BaseTypeProvider(), PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0221'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (psiElement.isThisVariableInPestTest()) return TEST_CASE_TYPE

        return null
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }

    companion object {
        private val TEST_CASE_TYPE = PhpType().add("\\PHPUnit\\Framework\\TestCase")
    }
}
package com.pestphp.pest.types

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4

class ThisTypeProvider: PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0221'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if ((psiElement as? Variable)?.name != "this") return null

        val closure = PsiTreeUtil.getParentOfType(psiElement, Function::class.java)

        if (closure == null || !closure.isClosure) return null

        val parameterList = closure.parent?.parent as? ParameterList ?: return null

        if (closure.parent != parameterList.getParameter(1)) return null

        if (parameterList.parent !is FunctionReferenceImpl) return null

        if (!PEST_TEST_FUNCTION_NAMES.contains((parameterList.parent as FunctionReferenceImpl).name)) return null

        return TEST_CASE_TYPE
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }

    companion object {
        private val PEST_TEST_FUNCTION_NAMES: Set<String> = setOf("it", "test")
        private val TEST_CASE_TYPE = PhpType().add("\\PHPUnit\\Framework\\TestCase")
    }
}
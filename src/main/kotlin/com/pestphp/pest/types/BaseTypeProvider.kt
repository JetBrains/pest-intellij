package com.pestphp.pest.types

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

@Suppress("UnnecessaryAbstractClass")
abstract class BaseTypeProvider {
    protected fun PsiElement?.isThisVariableInPestTest(): Boolean {
        if ((this as? Variable)?.name != "this") return false

        val closure = PsiTreeUtil.getParentOfType(this, Function::class.java)

        if (closure == null || !closure.isClosure) return false

        val parameterList = closure.parent?.parent as? ParameterList ?: return false

        if (closure.parent != parameterList.getParameter(1)) return false

        if (parameterList.parent !is FunctionReferenceImpl) return false

        if (!PEST_TEST_FUNCTION_NAMES.contains((parameterList.parent as FunctionReferenceImpl).name)) return false

        return true
    }

    companion object {
        private val PEST_TEST_FUNCTION_NAMES: Set<String> = setOf("it", "test")
    }
}

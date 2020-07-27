package com.pestphp.pest.types

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

@Suppress("UnnecessaryAbstractClass")
abstract class BaseTypeProvider {
    protected inline fun PsiElement?.isThisVariableInPest(condition: (FunctionReferenceImpl) -> Boolean): Boolean {
        if ((this as? Variable)?.name != "this") return false

        val closure = PsiTreeUtil.getParentOfType(this, Function::class.java)

        if (closure == null || !closure.isClosure) return false

        val parameterList = closure.parent?.parent as? ParameterList ?: return false

        if (parameterList.parent !is FunctionReferenceImpl) return false

        return condition(parameterList.parent as FunctionReferenceImpl)
    }
}

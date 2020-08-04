package com.pestphp.pest

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType

inline fun PsiElement?.isThisVariableInPest(condition: (FunctionReferenceImpl) -> Boolean): Boolean {
    if ((this as? Variable)?.name != "this") return false

    val closure = PsiTreeUtil.getParentOfType(this, Function::class.java)

    if (closure == null || !closure.isClosure) return false

    val parameterList = closure.parent?.parent as? ParameterList ?: return false

    if (parameterList.parent !is FunctionReferenceImpl) return false

    return condition(parameterList.parent as FunctionReferenceImpl)
}

fun PsiFile.getAllBeforeThisAssignments(): List<AssignmentExpression> {
    return this.firstChild.children
        .filterIsInstance<Statement>()
        .mapNotNull { it.firstChild }
        .filterIsInstance<FunctionReferenceImpl>()
        .filter { it.isPestBeforeFunction() }
        .flatMap { it.getThisStatements() }
}

private val cacheKey = Key<CachedValue<List<AssignmentExpression>>>("com.pestphp.pest_assignments")
private fun FunctionReferenceImpl.getThisStatements(): List<AssignmentExpression> {
    return CachedValuesManager.getCachedValue(this, cacheKey) {
        val result = PsiTreeUtil.findChildrenOfType(this.parameterList?.getParameter(0), AssignmentExpression::class.java)
            .filter { ((it.variable as? FieldReference)?.classReference as? Variable)?.name == "this" }

        CachedValueProvider.Result.create(result, this)
    }
}

fun FunctionReference.getUsesPhpType(): PhpType? {
    parameters.mapNotNull {
        val classRef = it as? ClassConstantReference ?: return@mapNotNull null

        if (classRef.fqn != "\\class") return@mapNotNull null

        (classRef.classReference as? ClassReference)?.fqn
    }.apply {
        if (this.isEmpty()) return null

        val res = PhpType()

        this.forEach {
            res.add(it)
        }

        return res
    }
}
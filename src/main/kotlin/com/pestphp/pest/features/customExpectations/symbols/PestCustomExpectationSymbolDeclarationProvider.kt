@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations.symbols

import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.features.customExpectations.toMethod

class PestCustomExpectationSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
    override fun getDeclarations(element: PsiElement, offsetInElement: Int): Collection<PsiSymbolDeclaration> {
        val possibleExtensionName = element as? StringLiteralExpression ?: return emptyList()
        if (possibleExtensionName.parent !is ParameterList || possibleExtensionName.contents.isEmpty()) return emptyList()
        val methodReference = possibleExtensionName.parent.parent as? MethodReferenceImpl ?: return emptyList()
        val methodDescriptor = methodReference.toMethod() ?: return emptyList()
        if (methodReference.name == "extend" &&
            PhpType.intersectsGlobal(element.project, methodReference.classReference!!.type, PEST_EXPECTATION_TYPE)
        ) {
            return listOf(PestCustomExpectationSymbolDeclaration(element, methodDescriptor))
        }
        return emptyList()
    }
}

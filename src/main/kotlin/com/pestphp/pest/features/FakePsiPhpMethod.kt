package com.pestphp.pest.features

import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import com.intellij.util.Processor
import com.jetbrains.php.PhpPresentationUtil
import com.jetbrains.php.codeInsight.PhpScope
import com.jetbrains.php.codeInsight.controlFlow.PhpControlFlow
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment
import com.jetbrains.php.lang.psi.PhpExpressionCodeFragmentImpl.CodeFragmentEmptyScope
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.features.customExpectations.expectationType
import javax.swing.Icon
import com.pestphp.pest.features.customExpectations.generators.Method as ExpectationMethod

class FakePsiPhpMethod(val method: ExpectationMethod, val phpClass: PhpClass): FakePsiElement(), Method {
    private val scope: CodeFragmentEmptyScope = CodeFragmentEmptyScope("PestCustomException")

    override fun getPresentation(): ItemPresentation? {
        return PhpPresentationUtil.getMethodPresentation(this)
    }

    override fun getIcon(): Icon {
        return MethodImpl.getIcon(this)
    }

    override fun getParent(): PsiElement {
        return containingClass
    }

    override fun getName(): String {
        return method.name
    }

    override fun getNameIdentifier(): PsiElement? {
        return null
    }

    override fun getFirstPsiChild(): PhpPsiElement? {
        return null
    }

    override fun getNextPsiSibling(): PhpPsiElement? {
        return null
    }

    override fun getPrevPsiSibling(): PhpPsiElement? {
        return null
    }

    override fun getType(): PhpType {
        return expectationType
        return method.returnType // TODO: use return type
    }

    override fun getNameNode(): ASTNode? {
        return null
    }

    override fun getNameCS(): CharSequence {
        return this.name
    }

    override fun getDocComment(): PhpDocComment? {
        return null
    }

    override fun processDocs(processor: Processor<PhpDocComment>) {
    }

    override fun getFQN(): String {
        val phpClass = this.containingClass

        return "${phpClass.fqn}.${this.name}"
    }

    override fun getNamespaceName(): String {
        return this.containingClass.namespaceName
    }

    override fun isDeprecated(): Boolean {
        return false
    }

    override fun isInternal(): Boolean {
        return false
    }

    override fun getModifier(): PhpModifier {
        return PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC
    }

    override fun getContainingClass(): PhpClass {
        return phpClass
    }

    override fun getControlFlow(): PhpControlFlow {
        return scope.controlFlow
    }

    override fun getPredefinedVariables(): MutableSet<CharSequence> {
       return mutableSetOf()
    }

    override fun getScope(): PhpScope {
        return scope
    }

    override fun getTypeDeclaration(): PhpReturnType? {
        return null
    }

    override fun getAttributes(): MutableCollection<PhpAttribute> {
        return mutableListOf()
    }

    override fun getParameters(): Array<Parameter> {
        return method.parameters
            .map { FakePsiPhpParameter(it, this) }
            .toTypedArray()
    }

    override fun getParameter(index: Int): Parameter? {
        if (method.parameters.size <= index) {
            return null
        }

        return FakePsiPhpParameter(method.parameters[index], this)
    }

    override fun hasRefParams(): Boolean {
        return this.parameters.any { it.isPassByRef }
    }

    override fun isClosure(): Boolean {
        return false
    }

    override fun getLocalType(interactive: Boolean): PhpType {
        return type
    }

    override fun getReturnType(): PhpReturnType? {
        return null
    }

    override fun getDocExceptions(): MutableCollection<String> {
        return mutableListOf()
    }

    override fun getMethodType(allowAmbiguity: Boolean): Method.MethodType {
        return Method.MethodType.REGULAR_METHOD
    }

    override fun isStatic(): Boolean {
        return false
    }

    override fun isFinal(): Boolean {
        return false
    }

    override fun isAbstract(): Boolean {
        return false
    }

    override fun getAccess(): PhpModifier.Access {
        return PhpModifier.Access.PUBLIC
    }
}
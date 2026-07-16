package com.pestphp.pest.structureView

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestIcons
import com.pestphp.pest.getDirectNestedPestTests
import com.pestphp.pest.getInitialFunctionReference
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isDescribeFunction
import com.pestphp.pest.isPestTestReference

/**
 * Defines how the elements in the structure view
 * should be rendered.
 */
class PestStructureViewElement(val element: NavigatablePsiElement) : StructureViewTreeElement {
    override fun getPresentation(): ItemPresentation {
        if (!element.isPestTestReference()) {
            return element.presentation ?: PresentationData()
        }

        val isDescribe = (element.getInitialFunctionReference() as? FunctionReferenceImpl)?.isDescribeFunction() == true

        return PresentationData(
            element.getPestTestName(withParents = false),
            null,
            if (isDescribe) AllIcons.Nodes.TestGroup else PestIcons.Logo,
            null,
        )
    }

    override fun getChildren(): Array<TreeElement> {
        return (element as? FunctionReference)
            ?.getDirectNestedPestTests()
            ?.map { PestStructureViewElement(it) }
            ?.toTypedArray()
            ?: arrayOf()
    }

    override fun navigate(requestFocus: Boolean) {
        return element.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return element.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element.canNavigateToSource()
    }

    override fun getValue(): Any {
        return element
    }
}

package com.pestphp.pest.structureView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.pestphp.pest.PestIcons
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestReference

class PestStructureViewElement(val element: NavigatablePsiElement): StructureViewTreeElement {
    override fun getPresentation(): ItemPresentation {
        if (!element.isPestTestReference()) {
            return element.presentation ?: PresentationData()
        }

        return PresentationData(
            element.getPestTestName(),
            null,
            PestIcons.LOGO,
            null,
        )
    }

    override fun getChildren(): Array<TreeElement> {
        return arrayOf()
    }

    override fun navigate(requestFocus: Boolean) {
        return element.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return element.canNavigate();
    }

    override fun canNavigateToSource(): Boolean {
        return element.canNavigateToSource()
    }

    override fun getValue(): Any {
        return element
    }
}
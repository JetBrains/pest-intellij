package com.pestphp.pest.structureView

import com.intellij.ide.structureView.StructureViewExtension
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.getPestTests

class PestStructureViewExtension: StructureViewExtension {
    override fun getType(): Class<out PsiElement> {
        return PhpFile::class.java
    }

    override fun getChildren(parent: PsiElement?): Array<StructureViewTreeElement> {
        if (parent is PhpFile) {
            return parent.getPestTests()
                .map { PestStructureViewElement(it) }
                .toTypedArray()
        }

        return arrayOf()
    }

    override fun getCurrentEditorElement(editor: Editor?, parent: PsiElement?): Any? {
        return null
    }
}
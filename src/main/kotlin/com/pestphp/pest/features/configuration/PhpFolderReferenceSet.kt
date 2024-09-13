package com.pestphp.pest.features.configuration

import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.impl.PhpFileReferenceSet

class PhpFolderReferenceSet(element: PsiElement, argument: PhpPsiElement, provider: PsiReferenceProvider) : PhpFileReferenceSet(element, argument, provider) {
    override fun getReferenceCompletionFilter(): Condition<PsiFileSystemItem> {
        return FileReferenceSet.DIRECTORY_FILTER
    }

    override fun computeDefaultContexts(): MutableCollection<PsiFileSystemItem> {
        val containingFile = this.element.containingFile.originalFile
        val directory = containingFile.virtualFile.parent

        val fileSystemItems = toFileSystemItems(directory)

        if (fileSystemItems.isNotEmpty()) {
            return fileSystemItems
        }

        return super.computeDefaultContexts()
    }
}
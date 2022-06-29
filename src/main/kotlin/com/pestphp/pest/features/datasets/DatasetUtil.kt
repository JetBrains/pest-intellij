package com.pestphp.pest.features.datasets

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.ProjectScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.getRootPhpPsiElements
import com.pestphp.pest.realPath

fun PsiFile.isPestDatasetFile(): Boolean {
    return this.getRootPhpPsiElements()
        .any(PsiElement::isPestDataset)
}

fun PsiFile.isIndexedPestDatasetFile(): Boolean {
    return FileBasedIndex.getInstance().getValues(
        DatasetIndex.key,
        this.realPath,
        ProjectScope.getProjectScope(this.project)
    ).isNotEmpty()
}

fun PsiElement?.isPestDataset(): Boolean {
    return when (this) {
        null -> false
        is FunctionReferenceImpl -> this.isPestDatasetFunction()
        else -> false
    }
}

fun FunctionReferenceImpl.isPestDatasetFunction(): Boolean {
    return this.canonicalText in setOf("dataset")
}

fun FunctionReferenceImpl.getPestDatasetName(): String? {
    return (getParameter(0) as? StringLiteralExpression)?.contents
}

fun PsiFile.getDatasets(): List<FunctionReferenceImpl> {
    return this.getRootPhpPsiElements()
        .filter { it.isPestDataset() }
        .filterIsInstance<FunctionReferenceImpl>()
}
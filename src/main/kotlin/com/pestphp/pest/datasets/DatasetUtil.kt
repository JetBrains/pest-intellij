package com.pestphp.pest.datasets

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.getRootPhpPsiElements

fun PsiFile.isPestDatasetFile(): Boolean {
    return this.getRootPhpPsiElements()
        .any(PsiElement::isPestDataset)
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
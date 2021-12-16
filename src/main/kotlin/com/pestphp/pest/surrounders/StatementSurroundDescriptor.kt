package com.pestphp.pest.surrounders

import com.intellij.lang.surroundWith.SurroundDescriptor
import com.intellij.lang.surroundWith.Surrounder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpExpressionImpl
import com.jetbrains.php.surroundWith.PhpStatementSurroundDescriptor
import com.pestphp.pest.getPestTests

class StatementSurroundDescriptor: SurroundDescriptor {
    override fun getElementsToSurround(file: PsiFile, startOffset: Int, endOffset: Int): Array<PsiElement> {
        val range = TextRange(startOffset, endOffset)

        val insideTest = file.getPestTests()
            .any { it.textRange.contains(range) }

        if (!insideTest) {
            return arrayOf()
        }

        return PhpStatementSurroundDescriptor().getElementsToSurround(
            file, startOffset, endOffset
        )
    }

    override fun getSurrounders(): Array<Surrounder> {
        return arrayOf(ExpectStatementSurrounder())
    }

    override fun isExclusive(): Boolean {
        return false
    }
}
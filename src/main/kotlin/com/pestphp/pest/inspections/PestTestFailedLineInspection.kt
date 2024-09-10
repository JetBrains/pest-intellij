package com.pestphp.pest.inspections

import com.intellij.codeInsight.daemon.impl.CollectHighlightsUtil
import com.intellij.codeInspection.*
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.inspections.phpunit.PhpUnitTestFailedLineInspection
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.isPestTestReference
import com.pestphp.pest.runner.PestFailedLineManager

class PestTestFailedLineInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFunctionCall(functionCall: FunctionReference) {
                if (!functionCall.isPestTestReference()) return
                val failedLineManager = holder.project.getService(PestFailedLineManager::class.java)
                val failedLines = failedLineManager.getFailedLines(functionCall)
                if (failedLines.isEmpty()) return

                failedLines.forEach { failedLine ->
                    val file = functionCall.getContainingFile()
                    val parent = PhpUnitTestFailedLineInspection.findCommonParent(file, failedLine.textRange)
                    if (parent != null) {
                        val failedLineRange = getRangeForHighlighting(failedLine, file, parent)
                        val relativeTextRange = failedLineRange.shiftLeft(parent.textRange.startOffset)
                        if (relativeTextRange.startOffset >= parent.textLength) return
                        if (relativeTextRange.endOffset > parent.textLength) return
                        val quickFixes = mutableListOf(PhpUnitTestFailedLineInspection.RunActionFix(parent))
                        if (PhpUnitTestFailedLineInspection.DebugActionFix.isAvailable(parent, relativeTextRange.startOffset)) {
                            quickFixes.add(PhpUnitTestFailedLineInspection.DebugActionFix(parent))
                        }
                        val descriptor = InspectionManager.getInstance(holder.project).createProblemDescriptor(
                            parent, relativeTextRange, failedLine.error, ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            holder.isOnTheFly, *quickFixes.toTypedArray()
                        )
                        descriptor.setTextAttributes(CodeInsightColors.RUNTIME_ERROR)
                        holder.registerProblem(descriptor)
                    }
                }
            }
        }
    }

    private fun getRangeForHighlighting(failedLine: PestFailedLineManager.FailedLine, file: PsiFile, parent: PsiElement): TextRange {
        val range = failedLine.textRange
        val elements = CollectHighlightsUtil.getElementsInRange(file, range.startOffset, range.endOffset)
        if (elements.any { e -> e is PhpExpression } || elements.any { e -> e is Function && !e.isClosure() }) {
            return range
        }
        return parent.getTextRange()
    }
}
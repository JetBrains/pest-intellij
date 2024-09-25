package com.pestphp.pest.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpTestFailedLineInspectionBase
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.isPestTestReference
import com.pestphp.pest.runner.PestFailedLineManager

class PestTestFailedLineInspection : PhpTestFailedLineInspectionBase() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFunctionCall(functionCall: FunctionReference) {
                if (!functionCall.isPestTestReference()) return
                val failedLineManager = holder.project.getService(PestFailedLineManager::class.java)
                process(holder, functionCall, failedLineManager)
            }
        }
    }
}
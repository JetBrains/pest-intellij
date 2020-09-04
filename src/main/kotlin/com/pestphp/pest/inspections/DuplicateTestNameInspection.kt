package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests

class DuplicateTestNameInspection : PhpInspection() {
    companion object {
        private const val DESCRIPTION = "Pest test names must be unique in the same file."
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFile(file: PhpFile) {
                file.getPestTests()
                    .groupBy { it.getPestTestName() }
                    .filterKeys { it != null }
                    .filter { it.value.count() > 1 }
                    .forEach {
                        declareProblemType(holder, it.value)
                    }
            }
        }
    }

    @Suppress("SpreadOperator")
    private fun declareProblemType(holder: ProblemsHolder, tests: List<FunctionReference>) {
        tests.forEach {
            holder.registerProblem(
                it,
                DESCRIPTION,
                ProblemHighlightType.GENERIC_ERROR,
                *LocalQuickFix.EMPTY_ARRAY
            )
        }
    }
}

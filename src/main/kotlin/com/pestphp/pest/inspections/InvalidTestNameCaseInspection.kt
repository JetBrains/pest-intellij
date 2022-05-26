package com.pestphp.pest.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import net.pearx.kasechange.splitToWords

class InvalidTestNameCaseInspection : PhpInspection() {
    companion object {
        private const val DESCRIPTION = "Pest test names words must be space separated."
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFile(file: PhpFile) {
                file.getPestTests()
                    .groupBy { it.getPestTestName() }
                    .filterKeys { it != null }
                    .filterKeys { !it!!.contains(' ') }
                    .filterKeys { it!!.splitToWords().joinToString(" ") != it }
                    .forEach {
                        declareProblemType(holder, it.value)
                    }
            }
        }
    }

    private fun declareProblemType(holder: ProblemsHolder, tests: List<FunctionReference>) {
        tests
            .mapNotNull { it.getParameter(0) }
            .forEach {
                holder.registerProblem(
                    it,
                    DESCRIPTION,
                    ProblemHighlightType.WEAK_WARNING,
                    ChangeTestNameCasingQuickFix()
                )
            }
    }
}

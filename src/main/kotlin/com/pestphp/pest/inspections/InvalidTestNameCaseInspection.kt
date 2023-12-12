package com.pestphp.pest.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.util.text.NameUtilCore
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.PestBundle
import com.pestphp.pest.getInitialFunctionReference
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests

class InvalidTestNameCaseInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFile(file: PhpFile) {
                file.getPestTests()
                    .groupBy { it.getPestTestName() }
                    .filterKeys { it != null }
                    .filterKeys {
                        // Remove `it ` prefix from test names
                        val testName = if (it!!.startsWith("it ")) it.substring(3) else it

                        !testName.contains(' ') && NameUtilCore.splitNameIntoWords(testName).joinToString(" ") != testName
                    }
                    .forEach {
                        declareProblemType(holder, it.value)
                    }
            }
        }
    }

    private fun declareProblemType(holder: ProblemsHolder, tests: List<FunctionReference>) {
        tests
            .mapNotNull { it.getInitialFunctionReference()?.getParameter(0) }
            .forEach {
                holder.registerProblem(
                    it,
                    PestBundle.message("INSPECTION_INVALID_TEST_NAME_CASE"),
                    ProblemHighlightType.WEAK_WARNING,
                    ChangeTestNameCasingQuickFix()
                )
            }
    }
}

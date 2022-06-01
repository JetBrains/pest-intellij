package com.pestphp.pest.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.features.customExpectations.isExpectation

class MultipleExpectChainableInspection : PhpInspection() {
    companion object {
        private const val DESCRIPTION = "Multiple expect can be converted to chainable calls."
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpGroupStatement(groupStatement: GroupStatement) {
                var counter = 1
                groupStatement.statements
                    .filterIsInstance(Statement::class.java)
                    .groupBy {
                        if ((it.firstPsiChild as? MethodReference)?.type?.isExpectation(holder.project) != true) {
                            counter++
                            return@groupBy 0
                        }
                        counter
                    }
                    .toMutableMap()
                    // Drop index 0, as that is all non expect calls
                    .also { it.remove(0) }
                    // Filter all expect call groups with only one expect call
                    .filterValues { it.size >= 2 }
                    .forEach {
                        declareProblemType(holder, it.value)
                    }
            }

        }
    }

    @Suppress("SpreadOperator")
    private fun declareProblemType(holder: ProblemsHolder, statements: List<Statement>) {
        statements
            .forEach {
                holder.registerProblem(
                    it,
                    DESCRIPTION,
                    ProblemHighlightType.WEAK_WARNING,
                    ChangeMultipleExpectCallsToChainableQuickFix()
                )
            }
    }
}

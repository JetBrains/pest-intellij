package com.pestphp.pest.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.PestBundle
import com.pestphp.pest.features.customExpectations.isExpectation

class MultipleExpectChainableInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpGroupStatement(groupStatement: GroupStatement) {
                var counter = 1
                groupStatement.statements
                    .filterIsInstance(Statement::class.java)
                    .groupBy {
                        val methodReference = it.firstPsiChild as? MethodReference
                        if (methodReference?.text?.startsWith("expect") != true || !methodReference.type.isExpectation(holder.project)) {
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
                    PestBundle.message("INSPECTION_MULTIPLE_CHAINABLE_EXPECT_CALLS"),
                    ChangeMultipleExpectCallsToChainableQuickFix()
                )
            }
    }
}

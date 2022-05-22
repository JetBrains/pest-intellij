package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.pestphp.pest.customExpectations.isExpectation

class ChangeMultipleExpectCallsToChainableQuickFix: LocalQuickFix {
    companion object {
        const val QUICK_FIX_NAME = "Change multiple expect call into chain"
    }

    override fun getFamilyName(): String {
        return QUICK_FIX_NAME
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        var statement = descriptor.psiElement as? Statement ?: return
        var expectCall = statement.firstPsiChild as? MethodReference ?: return
        var replaceExpectCall = expectCall

        if (!expectCall.isExpectation()) {
            return
        }

        // Find the first expect call in the group
        while ((statement.prevPsiSibling as? Statement)?.isExpectation() == true) {
            statement = statement.prevPsiSibling as Statement
            expectCall = statement.firstPsiChild as MethodReference
            replaceExpectCall = expectCall
        }

        // Loop over all the next statement and merge together to one expect cal..
        var nextSibling = statement.nextPsiSibling as? Statement
        while (nextSibling != null) {
            val siblingMethodReference = nextSibling.firstPsiChild as? MethodReference ?: break

            if (!siblingMethodReference.isExpectation()) {
                break
            }

            // Replace expect with and on the next call.
            replaceExpectCall = PhpPsiElementFactory.createMethodReference(
                project,
                replaceExpectCall.text
                        + "\n->"
                        + siblingMethodReference.text.replaceFirst("expect", "and")
            )

            val oldSibling = nextSibling
            nextSibling = nextSibling.nextPsiSibling as? Statement
            oldSibling.delete()
        }

        expectCall.replace(replaceExpectCall)
    }
}
package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.customExpectations.CustomExpectationIndex
import com.pestphp.pest.customExpectations.extendName

class DuplicateCustomExpectationInspection : PhpInspection() {
    companion object {
        private const val DESCRIPTION = "Pest custom expectation names must be unique."
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpMethodReference(reference: MethodReference) {
                if (reference !is MethodReferenceImpl) {
                    return
                }

                FileBasedIndex.getInstance()
                    .getFileData(
                        CustomExpectationIndex.key,
                        reference.containingFile.virtualFile,
                        reference.project
                    )
                    .flatMap { it.value }
                    .filter { reference.extendName == it.name }
                    .let {
                        if (it.count() < 2) {
                            return
                        }

                        holder.registerProblem(
                            reference,
                            DESCRIPTION,
                            ProblemHighlightType.GENERIC_ERROR,
                            *LocalQuickFix.EMPTY_ARRAY
                        )
                    }
            }
        }
    }
}

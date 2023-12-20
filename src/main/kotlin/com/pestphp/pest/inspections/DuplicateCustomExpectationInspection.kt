package com.pestphp.pest.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.PestBundle
import com.pestphp.pest.features.customExpectations.KEY
import com.pestphp.pest.features.customExpectations.extendName

class DuplicateCustomExpectationInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpMethodReference(reference: MethodReference) {
                if (reference !is MethodReferenceImpl) {
                    return
                }
                if (reference.extendName == null) {
                    return
                }
                val hasDuplicates = FileBasedIndex.getInstance()
                    .getValues(KEY, reference.extendName!!, GlobalSearchScope.allScope(holder.project))
                    .flatten()
                    .count() > 1
                if (hasDuplicates) {
                    holder.registerProblem(
                        reference,
                        PestBundle.message("INSPECTION_DUPLICATE_CUSTOM_EXPECTATION"),
                        *LocalQuickFix.EMPTY_ARRAY
                    )
                }
            }
        }
    }
}

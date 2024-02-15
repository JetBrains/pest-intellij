package com.pestphp.pest.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.modcommand.ModCommandAction
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.annotator.PhpAnnotatorVisitor.createErrorAnnotation
import com.jetbrains.php.lang.annotator.PhpDeleteElementQuickFix
import com.jetbrains.php.lang.inspections.controlFlow.PhpNavigateToElementQuickFix
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.PestBundle
import com.pestphp.pest.features.customExpectations.KEY
import com.pestphp.pest.features.customExpectations.extendName
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests

class PestAnnotatorVisitor(
    private val holder: AnnotationHolder
) : PhpElementVisitor() {
    override fun visitPhpMethodReference(reference: MethodReference) {
        checkDuplicateCustomExpectations(reference)
    }

    private fun checkDuplicateCustomExpectations(reference: MethodReference) {
        if (reference !is MethodReferenceImpl) {
            return
        }
        val extendName = reference.extendName ?: return
        val duplicates = mutableListOf<MethodReference>()
        FileBasedIndex.getInstance().processValues(KEY, extendName, null, { file, offsets ->
            reference.manager.findFile(file)?.let { psiFile ->
                offsets.forEach { offset ->
                    val expectation = psiFile.findElementAt(offset)
                    val methodDescriptor = PhpPsiUtil.getParentOfClass(expectation, MethodReference::class.java)
                    if (methodDescriptor != null) {
                        duplicates.add(methodDescriptor)
                    }
                }
            }
            true
        }, GlobalSearchScope.allScope(reference.project))
        if (duplicates.size > 1) {
            val fixes = listOfNotNull(
                PhpDeleteElementQuickFix(reference.parent, PestBundle.message("QUICK_FIX_DELETE_CUSTOM_EXPECTATION", extendName)),
                getNavigateToCustomExpectationFix(duplicates, reference)
            )
            val builder = holder.newAnnotation(
                HighlightSeverity.WARNING,
                PestBundle.message("INSPECTION_DUPLICATE_CUSTOM_EXPECTATION")
            ).range(reference)
            fixes.forEach { fix -> builder.withFix(fix.asIntention()) }
            builder.create()
        }
    }

    private fun getNavigateToCustomExpectationFix(
        duplicates: List<MethodReference>,
        duplicate: MethodReference
    ): ModCommandAction? {
        val duplicateIndex = duplicates.indexOf(duplicate)
        if (duplicateIndex == -1) {
            return null
        }
        val nextElement = duplicates[(duplicateIndex + 1) % duplicates.size]
        return PhpNavigateToElementQuickFix(
            nextElement,
            PestBundle.message("INTENTION_NAVIGATE_TO_DUPLICATE_CUSTOM_EXPECTATION")
        )
    }

    override fun visitPhpFile(phpFile: PhpFile) {
        checkDuplicateTestNames(phpFile)
    }

    private fun checkDuplicateTestNames(file: PhpFile) {
        file.getPestTests(isSmart = true)
            .groupBy { it.getPestTestName() }
            .filterKeys { it != null }
            .filter { it.value.count() > 1 }
            .forEach { (_, tests) ->
                tests.forEachIndexed { index, test ->
                    val testName = test.getPestTestName() ?: return@forEachIndexed
                    createErrorAnnotation(holder, test, PestBundle.message("INSPECTION_DUPLICATE_TEST_NAME"),
                                          PhpDeleteElementQuickFix(test.parent, PestBundle.message("QUICK_FIX_DELETE_TEST", testName)),
                                          getNavigateToTestNameFix(tests, index))
                }
            }
    }

    private fun getNavigateToTestNameFix(duplicates: List<FunctionReference>, duplicateIndex: Int): ModCommandAction {
        val nextElement = duplicates[(duplicateIndex + 1) % duplicates.size]
        return PhpNavigateToElementQuickFix(
            nextElement,
            PestBundle.message("INTENTION_NAVIGATE_TO_DUPLICATE_TEST_NAME")
        )
    }
}
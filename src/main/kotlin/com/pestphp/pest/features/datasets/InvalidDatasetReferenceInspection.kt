package com.pestphp.pest.features.datasets

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.PestBundle
import com.pestphp.pest.getPestTests

class InvalidDatasetReferenceInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFile(file: PhpFile) {
                val localDatasets = file.getDatasets()
                    .mapNotNull { it.getPestDatasetName() }
                // Get all shared datasets
                val fileBasedIndex = FileBasedIndex.getInstance()
                val sharedDatasets = fileBasedIndex.getAllKeys(key, file.project)
                    .map {
                        fileBasedIndex.getValues(
                          key,
                          it,
                          GlobalSearchScope.projectScope(file.project)
                        )
                    }
                    .flatten()
                    .flatten()

                file.getPestTests()
                    // Has to be a method reference, as else there is no dataset
                    .asSequence()
                    .filterIsInstance<MethodReferenceImpl>()
                    .filter { it.name == "with" }
                    .mapNotNull { it.parameters.getOrNull(0) }
                    .filterIsInstance<StringLiteralExpression>()
                    .filter { it.contents !in localDatasets && it.contents !in sharedDatasets }
                    .toList()
                    .forEach {
                        declareProblemType(
                            holder,
                            it
                        )
                    }
            }
        }
    }

    @Suppress("SpreadOperator")
    private fun declareProblemType(holder: ProblemsHolder, datasetName: StringLiteralExpression) {
        holder.registerProblem(
            datasetName,
            PestBundle.message("INSPECTION_INVALID_DATASET_REFERENCE"),
            *LocalQuickFix.EMPTY_ARRAY
        )
    }
}

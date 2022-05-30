package com.pestphp.pest.datasets

import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl


/**
 * Used to make a reference between a string and a dataset function call
 */
class DatasetReference(
    element: StringLiteralExpression
) : PsiReferenceBase<StringLiteralExpression>(element), PsiPolyVariantReference {
    override fun resolve(): PsiElement? {
        return multiResolve(false).firstOrNull()?.element
    }

    override fun getVariants(): Array<Any> {
        return getAllDatasets()
            .mapNotNull { it.getPestDatasetName() }
            .toTypedArray()
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val datasetName = element.contents
        val foundDatasets = mutableListOf<FunctionReferenceImpl>()

        fileBasedIndex.getAllKeys(
            DatasetIndex.key,
            element.project
        ).forEach { key ->
            fileBasedIndex.processValues(
                DatasetIndex.key,
                key,
                null,
                { file, datasets ->
                    if (datasetName !in datasets) {
                        return@processValues true
                    }

                    // Add all shared datasets which matches
                    PsiManager.getInstance(element.project).findFile(file)!!
                        .getDatasets()
                        .filter { it.getPestDatasetName() == datasetName }
                        .forEach { foundDatasets.add(it) }

                    true
                },
                GlobalSearchScope.projectScope(element.project)
            )
        }

        // Add all local datasets which matches
        element.containingFile
            .getDatasets()
            .filter { it.getPestDatasetName() == datasetName }
            .forEach { foundDatasets.add(it) }

        return foundDatasets.map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    private fun getAllDatasets(): MutableList<FunctionReferenceImpl> {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val foundDatasets = mutableListOf<FunctionReferenceImpl>()

        fileBasedIndex.getAllKeys(
            DatasetIndex.key,
            element.project
        ).forEach { key ->
            fileBasedIndex.processValues(
                DatasetIndex.key,
                key,
                null,
                { file, _ ->
                    // Add all datasets
                    PsiManager.getInstance(element.project).findFile(file)!!
                        .getDatasets()
                        .forEach { foundDatasets.add(it) }

                    true
                },
                GlobalSearchScope.projectScope(element.project)
            )
        }

        // Add all local datasets which matches
        element.containingFile
            .getDatasets()
            .forEach { foundDatasets.add(it) }

        return foundDatasets
    }
}
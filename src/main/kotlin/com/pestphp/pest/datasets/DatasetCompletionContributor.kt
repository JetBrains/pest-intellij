package com.pestphp.pest.datasets

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.getAllBeforeThisAssignments
import com.pestphp.pest.getRootPhpPsiElements
import com.pestphp.pest.isAnyPestFunction
import com.pestphp.pest.isThisVariableInPest


class DatasetCompletionContributor: CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withParent(
                StringLiteralExpression::class.java
            ),
            DataSetCompletionProvider()
        )
    }

    class DataSetCompletionProvider: CompletionProvider<CompletionParameters>(), GotoDeclarationHandler {
        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet
        ) {
            val fileBasedIndex = FileBasedIndex.getInstance()

            // Get all shared datasets
            val sharedDatasets = fileBasedIndex.getAllKeys(DatasetIndex.key, parameters.originalFile.project)
                .map { fileBasedIndex.getValues(
                    DatasetIndex.key,
                    it,
                    GlobalSearchScope.projectScope(parameters.originalFile.project)
                ) }
                .flatten()
                .flatten()

            // Get all datasets in the same file
            val localDatasets = parameters.originalFile
                .getRootPhpPsiElements()
                .filter { it.isPestDataset() }
                .filterIsInstance<FunctionReferenceImpl>()
                .mapNotNull { it.getPestDatasetName() }

            listOf(
                *sharedDatasets.toTypedArray(),
                *localDatasets.toTypedArray(),
            ).forEach {
                result.addElement(
                    LookupElementBuilder.create(it)
                )
            }
        }

        override fun getGotoDeclarationTargets(
            sourceElement: PsiElement?,
            offset: Int,
            editor: Editor
        ): Array<PsiElement> {
            if (sourceElement?.elementType !in listOf(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE, PhpTokenTypes.STRING_LITERAL)) {
                return PsiElement.EMPTY_ARRAY
            }

            val parent = sourceElement?.parent
            if (parent !is StringLiteralExpression) {
                return PsiElement.EMPTY_ARRAY
            }

            val fileBasedIndex = FileBasedIndex.getInstance()
            val datasetName = parent.contents

            val foundDatasets = mutableListOf<PsiElement>()

            fileBasedIndex.getAllKeys(
                DatasetIndex.key,
                parent.project
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
                        PsiManager.getInstance(parent.project).findFile(file)!!
                            .getRootPhpPsiElements()
                            .filter { it.isPestDataset() }
                            .filterIsInstance<FunctionReferenceImpl>()
                            .filter { it.getPestDatasetName() == datasetName }
                            .forEach { foundDatasets.add(it) }

                        true
                    },
                    GlobalSearchScope.projectScope(parent.project)
                )
            }

            // Add all local datasets which matches
            parent.containingFile
                .getRootPhpPsiElements()
                .filter { it.isPestDataset() }
                .filterIsInstance<FunctionReferenceImpl>()
                .filter { it.getPestDatasetName() == datasetName }
                .forEach { foundDatasets.add(it) }

            return foundDatasets.toTypedArray()
        }
    }
}
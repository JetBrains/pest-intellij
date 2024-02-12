package com.pestphp.pest.features.datasets

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.modcommand.ActionContext
import com.intellij.modcommand.ModPsiUpdater
import com.intellij.modcommand.PsiUpdateModCommandAction
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.PestBundle
import com.pestphp.pest.getInitialFunctionReference
import com.pestphp.pest.getRootPhpPsiElements
import com.pestphp.pest.goto.getDatasetUsages
import com.pestphp.pest.inspections.convertTestNameToSentenceCase
import com.pestphp.pest.inspections.isInvalidNameCase

class InvalidDatasetNameCaseInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpFile(file: PhpFile) {
                val localDatasets = file.getRootPhpPsiElements()
                    .filter { it.isPestDataset() }
                    .filterIsInstance<FunctionReferenceImpl>()

                localDatasets.groupBy { it.getPestDatasetName() }
                    .filterKeys { datasetName ->
                        datasetName != null && isInvalidNameCase(datasetName)
                    }
                    .forEach {
                        declareProblemType(holder, it.value)
                    }
            }
        }
    }

    private fun declareProblemType(holder: ProblemsHolder, datasets: List<FunctionReference>) {
        datasets.mapNotNull { it.getInitialFunctionReference()?.getParameter(0) }
            .filterIsInstance<StringLiteralExpression>()
            .forEach {
                holder.problem(it, PestBundle.message("INSPECTION_INVALID_DATASET_NAME_CASE"))
                    .fix(ChangeDatasetNameCasingQuickFix(it))
                    .register()
            }
    }

    private class ChangeDatasetNameCasingQuickFix(
        datasetDeclarationName: StringLiteralExpression
    ) : PsiUpdateModCommandAction<StringLiteralExpression>(datasetDeclarationName) {
        override fun getFamilyName(): String {
            return PestBundle.message("QUICK_FIX_CHANGE_DATASET_NAME_CASING")
        }

        override fun invoke(context: ActionContext, datasetNamePsiElement: StringLiteralExpression, updater: ModPsiUpdater) {
            val sentenceCaseDatasetName = convertTestNameToSentenceCase(datasetNamePsiElement.contents)
            val newNameParameter = PhpPsiElementFactory.createStringLiteralExpression(
                datasetNamePsiElement.project,
                sentenceCaseDatasetName,
                true
            )

            val datasetUsages = getDatasetUsages(datasetNamePsiElement)?.map { updater.getWritable(it) }
            datasetUsages?.forEach {
                val testWithDataset = it as? MethodReference ?: return@forEach
                val nameParameter = testWithDataset.getParameter(0) as? StringLiteralExpression ?: return@forEach

                nameParameter.replace(newNameParameter.copy())
            }

            datasetNamePsiElement.replace(newNameParameter.copy())
        }
    }
}
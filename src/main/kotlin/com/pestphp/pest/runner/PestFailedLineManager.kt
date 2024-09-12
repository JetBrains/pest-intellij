package com.pestphp.pest.runner

import com.intellij.execution.TestStateStorage
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.phpunit.PhpUnitTestRunLineMarkerProvider
import com.jetbrains.php.testFramework.PhpTestFrameworkFailedLineManager
import com.pestphp.pest.configuration.PestLocationProvider
import com.pestphp.pest.features.datasets.isDatasetCall
import com.pestphp.pest.getPestTestName
import java.nio.file.Paths

@Service(Service.Level.PROJECT)
class PestFailedLineManager(
    project: Project
) : PhpTestFrameworkFailedLineManager(project), FileEditorManagerListener {
    override fun getTestLocationUrl(psiElement: PsiElement): String? {
        if (psiElement !is FunctionReference) return null
        return getLocationUrl(psiElement.containingFile, psiElement)
    }

    override fun getRecordsForTest(psiElement: PsiElement): List<TestStateStorage.Record> {
        val testLocationUrl = getTestLocationUrl(psiElement) ?: return emptyList()
        val testStateRecord = TestStateStorage.getInstance(psiElement.project).getState(testLocationUrl) ?: return emptyList()

        val project = psiElement.getProject()
        val records = mutableListOf(testStateRecord)
        if (testStateRecord.failedLine == -1 && (psiElement.parent as? MethodReference)?.isDatasetCall() == true) {
            val allRecordLocationUrls = TestStateStorage.getInstance(project).keys
            val dataSetRecords: List<TestStateStorage.Record> = allRecordLocationUrls
                .asSequence()
                .filterNotNull()
                .filter { recordLocationUrl -> recordLocationUrl.startsWith("$testLocationUrl with data set ") }
                .map { recordLocationUrl -> TestStateStorage.getInstance(project).getState(recordLocationUrl) }
                .filterNotNull()
                .filter { record -> record.failedLine != -1 }
                .toList()

            records.addAll(dataSetRecords)
        }
        return records
    }

    private fun getLocationUrl(containingFile: PsiFile, functionCall: FunctionReference): String {
        return getLocationUrl(containingFile) + "::" + functionCall.getPestTestName()
    }

    private fun getLocationUrl(psiFile: PsiFile): String {
        val absoluteFilePath = PhpUnitTestRunLineMarkerProvider.getFilePathDeploymentAware(psiFile.getContainingFile())
        val path = if (absoluteFilePath.startsWith(psiFile.getProject().getBasePath() ?: "")) {
            Paths.get(psiFile.project.basePath ?: "").relativize(Paths.get(absoluteFilePath)).toString()
        } else {
            absoluteFilePath
        }
        return "${PestLocationProvider.PROTOCOL_ID}://" + path
    }
}
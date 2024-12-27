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
import com.jetbrains.php.phpunit.PhpUnitTestRunLineMarkerProvider.createPathMapper
import com.jetbrains.php.testFramework.PhpTestFrameworkFailedLineManager
import com.pestphp.pest.configuration.PestLocationProvider
import com.pestphp.pest.features.datasets.isDatasetCall
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.withoutFirstFileSeparator

@Service(Service.Level.PROJECT)
class PestFailedLineManager(
    project: Project
) : PhpTestFrameworkFailedLineManager(project), FileEditorManagerListener {
    override fun getTestLocationUrl(testElement: PsiElement): String? {
        if (testElement !is FunctionReference) return null
        return getLocationUrl(testElement.containingFile, testElement)
    }

    override fun getRecordsForTest(testElement: PsiElement): List<TestStateStorage.Record> {
        val testLocationUrl = getTestLocationUrl(testElement) ?: return emptyList()
        val testStateRecord = TestStateStorage.getInstance(testElement.project).getState(testLocationUrl) ?: return emptyList()

        val project = testElement.getProject()
        val records = mutableListOf(testStateRecord)
        if (testStateRecord.failedLine == -1 && (testElement.parent as? MethodReference)?.isDatasetCall() == true) {
            val allRecordLocationUrls = TestStateStorage.getInstance(project).keys
            val dataSetRecords: List<TestStateStorage.Record> = allRecordLocationUrls
                .asSequence()
                .filterNotNull()
                .filter { recordLocationUrl -> isLocationUrlWithNamedDatasetValue(recordLocationUrl, testLocationUrl) }
                .map { recordLocationUrl -> TestStateStorage.getInstance(project).getState(recordLocationUrl) }
                .filterNotNull()
                .filter { record -> record.failedLine != -1 }
                .toList()

            records.addAll(dataSetRecords)
        }
        return records
    }

    private fun isLocationUrlWithNamedDatasetValue(recordLocationUrl: String, testLocationUrl: String): Boolean =
        recordLocationUrl.startsWith("$testLocationUrl with data set \"dataset")

    private fun getLocationUrl(containingFile: PsiFile, functionCall: FunctionReference): String =
        getLocationUrl(containingFile) + "::" + functionCall.getPestTestName()
}

internal fun getLocationUrl(psiFile: PsiFile): String {
    return "${PestLocationProvider.PROTOCOL_ID}://${
        PhpUnitTestRunLineMarkerProvider.getFilePathDeploymentAware(psiFile)
            .removePrefix(getProjectPathDeploymentAware(psiFile.project)).withoutFirstFileSeparator
    }"
}

private fun getProjectPathDeploymentAware(project: Project): String {
    val projectPath = project.basePath ?: return ""
    val remoteMapper = createPathMapper(project)
    return if (remoteMapper.canProcess(projectPath)) {
        remoteMapper.process(projectPath)
    } else {
        projectPath
    }
}
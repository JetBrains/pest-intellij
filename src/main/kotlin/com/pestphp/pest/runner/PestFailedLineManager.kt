package com.pestphp.pest.runner

import com.intellij.execution.TestStateStorage
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiFileRange
import com.intellij.util.DocumentUtil
import com.intellij.util.containers.FactoryMap
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.phpunit.PhpUnitFailedLineManager
import com.jetbrains.php.phpunit.PhpUnitTestRunLineMarkerProvider
import com.pestphp.pest.configuration.PestLocationProvider
import com.pestphp.pest.getPestTestName
import java.nio.file.Paths
import java.util.*

@Service(Service.Level.PROJECT)
class PestFailedLineManager(
    project: Project
) : FileEditorManagerListener {
    private val cache: MutableMap<VirtualFile, MutableMap<String, CachedFailedTextRange>> = FactoryMap.create { mutableMapOf() }

    init {
        project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this)
    }

    fun getFailedLines(functionCall: FunctionReference): List<FailedLine> {
        val project = functionCall.getProject()
        val url = getLocationHint(functionCall.containingFile, functionCall)
        val singleState = TestStateStorage.getInstance(project).getState(url) ?: return emptyList()

        val states = mutableListOf<TestStateStorage.Record>()
        states.add(singleState)

        if (singleState.failedLine == -1) { // Process datasets ... Take a look at the output for Pest with datasets
            val allStates = TestStateStorage.getInstance(project).keys
            val dataSetStates: List<TestStateStorage.Record> = allStates
                .asSequence()
                .filterNotNull()
                .filter { state -> state.startsWith("$url with data set ") }
                .map { state -> TestStateStorage.getInstance(project).getState(state) }
                .filterNotNull()
                .filter { record -> record.failedLine != -1 }
                .toList()

            states.addAll(dataSetStates)
        }

        val failedLines = mutableListOf<FailedLine>()
        states.forEach { state ->
            val cachedMap = cache[functionCall.getContainingFile().getVirtualFile()]
            val cachedValue = cachedMap?.get(url) ?: CachedFailedTextRange(state.date, null, state.failedLine)
            val pointerManager = SmartPointerManager.getInstance(project)
            val document = PsiDocumentManager.getInstance(functionCall.getProject()).getDocument(functionCall.getContainingFile())
            if (document == null || state.failedLine < 0 || state.failedLine > document.lineCount) {
                if (cachedValue.rangePointer != null) {
                    pointerManager.removePointer(cachedValue.rangePointer!!)
                    cachedValue.rangePointer = null
                }
                return@forEach
            }
            if (cachedValue.date == state.date && cachedValue.failedLine == state.failedLine && cachedValue.rangePointer != null) {
                val range = cachedValue.rangePointer!!.range
                if (range == null) {
                    return@forEach
                }

                failedLines.add(FailedLine(state.errorMessage, TextRange.create(range)))
                return@forEach
            }
            cachedMap?.putIfAbsent(url, cachedValue)
            val lineTextRange = DocumentUtil.getLineTextRange(document, state.failedLine - 1)
            val text = document.getText(lineTextRange)
            if (text == state.failedMethod) {
                val contentRange = PhpUnitFailedLineManager.getRangeAfterTrimSpaces(text, lineTextRange)
                if (cachedValue.rangePointer != null) pointerManager.removePointer(cachedValue.rangePointer!!)
                cachedValue.rangePointer = pointerManager.createSmartPsiFileRangePointer(functionCall.getContainingFile(), contentRange)
                cachedValue.date = state.date

                failedLines.add(FailedLine(state.errorMessage, contentRange))
            }
        }
        return failedLines
    }

    private fun getLocationHint(containingFile: PsiFile, functionCall: FunctionReference): String {
        return getLocationHint(containingFile) + "::" + functionCall.getPestTestName()
    }

    private fun getLocationHint(psiFile: PsiFile): String {
        val absoluteFilePath = PhpUnitTestRunLineMarkerProvider.getFilePathDeploymentAware(psiFile.getContainingFile())
        val path = if (absoluteFilePath.startsWith(psiFile.getProject().getBasePath() ?: "")) {
            Paths.get(psiFile.project.basePath ?: "").relativize(Paths.get(absoluteFilePath)).toString()
        } else {
            absoluteFilePath
        }
        return "${PestLocationProvider.PROTOCOL_ID}://" + path
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        val cachedValue: Map<String, CachedFailedTextRange>? = cache[file]
        if (cachedValue != null) {
            val pointerManager = SmartPointerManager.getInstance(source.project)
            cachedValue.forEach { (_, value) ->
                if (value.rangePointer != null) pointerManager.removePointer(
                    value.rangePointer!!
                )
            }
            cache.remove(file)
        }
    }

    data class FailedLine internal constructor(@NlsSafe val error: String, val textRange: TextRange)

    data class CachedFailedTextRange internal constructor(var date: Date, var rangePointer: SmartPsiFileRange?, val failedLine: Int)
}
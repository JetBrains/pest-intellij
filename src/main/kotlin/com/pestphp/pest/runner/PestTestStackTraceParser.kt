package com.pestphp.pest.runner

import com.intellij.execution.testframework.sm.runner.ui.TestStackTraceParser
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.util.DocumentUtil
import com.pestphp.pest.configuration.PestLocationProvider

fun parse(url: String,
          stacktrace: String?,
          errorMessage: String?,
          locator: PestLocationProvider,
          project: Project): PestTestStackTraceParser {
    if (stacktrace == null) return PestTestStackTraceParser(errorMessage)
    val lines = stacktrace.split("\n")
    if (lines.isEmpty()) return PestTestStackTraceParser(errorMessage)
    val realErrorMessage = if (errorMessage.isNullOrEmpty()) lines[0] else errorMessage

    val path = url.removePrefix("${PestLocationProvider.PROTOCOL_ID}://").substringBefore( "::")
    val lastLine = lines.last().trim { it <= ' ' }.substringAfter("at ")
    if (path == url || !lastLine.startsWith(path)) return PestTestStackTraceParser(realErrorMessage)
    val failedLine = StringUtil.parseInt(lastLine.substring(path.length + 1), -1)
    val failedLineText = if (failedLine > 0) getLineText(path, failedLine, project, locator) else null
    return PestTestStackTraceParser(failedLine, failedLineText, realErrorMessage, null)
}

private fun getLineText(
    path: String,
    line: Int,
    project: Project,
    locator: PestLocationProvider
): String? {
    val fileUrl = locator.calculateFileUrl(path)
    val vFile = locator.pathMapper.getLocalFile(fileUrl)
    if (vFile == null) return null
    val psiFile = PsiManager.getInstance(project).findFile(vFile)
    if (psiFile == null) return null
    val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
    if (document == null) return null
    if (line > document.lineCount) return null
    val range = DocumentUtil.getLineTextRange(document, line - 1)
    return document.getText(range)
}

class PestTestStackTraceParser(
    failedLine: Int,
    failedMethodName: String?,
    errorMessageName: String?,
    topLocationLine: String?,
) : TestStackTraceParser(failedLine, failedMethodName, errorMessageName, topLocationLine) {
    constructor(errorMessage: String?) : this(-1, null, errorMessage, null)
}
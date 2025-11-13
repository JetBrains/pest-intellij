package com.pestphp.pest.runner

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.TextRange
import com.pestphp.pest.PestBundle
import com.pestphp.pest.configuration.PestRunConfigurationType
import java.io.IOException
import java.nio.charset.StandardCharsets

class PestPressToContinueAction : DumbAwareAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val descriptor = e.getData(LangDataKeys.RUN_CONTENT_DESCRIPTOR)
        val processHandler = descriptor?.processHandler
        e.presentation.setText(PestBundle.messagePointer("action.press.to.continue.text"))
        e.presentation.isVisible = descriptor?.runConfigurationTypeId == PestRunConfigurationType.instance.id
        e.presentation.isEnabled = processHandler != null &&
            !processHandler.isProcessTerminated &&
            !processHandler.isProcessTerminating &&
            getInnerConsoleViewImpl(descriptor)?.let { shouldEnableAndPrintHint(it) } == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val descriptor = e.getData(LangDataKeys.RUN_CONTENT_DESCRIPTOR) ?: return
        val processHandler = descriptor.processHandler ?: return
        val processInput = processHandler.processInput ?: return
        processInput.let { stream ->
            try {
                stream.write("\n".toByteArray(StandardCharsets.UTF_8))
                stream.flush()
            } catch (io: IOException) {
                logger.warn("Failed to write to process stdin for Pest Press to continue", io)
            }
        }
    }

    private fun getInnerConsoleViewImpl(descriptor: RunContentDescriptor): ConsoleViewImpl? {
        val baseConsole = descriptor.executionConsole as? BaseTestsOutputConsoleView
        return baseConsole?.console as? ConsoleViewImpl
    }

    private fun readLastNonEmptyLineOrEmpty(view: ConsoleViewImpl): String {
        val editor = view.editor ?: return ""
        val doc = editor.document
        var line = doc.lineCount - 1
        while (line >= 0) {
            val start = doc.getLineStartOffset(line)
            val end = doc.getLineEndOffset(line)
            val text = doc.getText(TextRange(start, end))
            if (text.any { !it.isWhitespace() }) {
                return text
            }
            line--
        }
        return ""
    }

    private fun shouldEnableAndPrintHint(view: ConsoleViewImpl): Boolean {
        val line = readLastNonEmptyLineOrEmpty(view)
        if (line.contains(PROMPT)) {
            view.print("\n  $HINT\n", ConsoleViewContentType.SYSTEM_OUTPUT)
            return true
        }
        return line.contains(HINT)
    }

    internal companion object {
        private val logger = Logger.getInstance(PestPressToContinueAction::class.java)
        internal const val PROMPT: String = "Press any key to continue"
        internal const val HINT: String = "To continue, click \"Continue Test Run\" on the test results' toolbar."
    }
}

package com.pestphp.pest.runner

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.TestFrameworkRunningModel
import com.intellij.execution.testframework.ToggleModelAction
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.util.config.BooleanProperty
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestIcons
import java.io.IOException
import java.nio.charset.StandardCharsets

class PestPressToContinueAction(
    private val props: TestConsoleProperties
) : ToggleModelAction(
    PestBundle.message("action.press.to.continue.text"),
    PestBundle.message("action.press.to.continue.description"),
    PestIcons.Run,
    props,
    BooleanProperty("pest.pressToContinue", false)
) {

    override fun setModel(model: TestFrameworkRunningModel) {
    }

    override fun isVisible(): Boolean {
        return props is PestConsoleProperties
    }

    override fun isEnabled(): Boolean {
        val descriptor = getRunContentDescriptor()
        return descriptor
            ?.processHandler
            ?.takeUnless { it.isProcessTerminated || it.isProcessTerminating || it.processInput == null }
            ?.let { getInnerConsoleViewImpl(descriptor) }
            ?.let { shouldEnableAndPrintHint(it) }
            ?: false
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        getRunContentDescriptor()
            ?.processHandler
            ?.processInput
            ?.let { stream ->
                try {
                    stream.write("\n".toByteArray(StandardCharsets.UTF_8))
                    stream.flush()
                } catch (io: IOException) {
                    logger.warn("Failed to write to process stdin for Pest Press to continue", io)
                }
            }
    }

    private fun getRunContentDescriptor(): RunContentDescriptor? {
        val ctx = DataManager.getInstance().getDataContext(props.console.component)
        return ctx.getData(LangDataKeys.RUN_CONTENT_DESCRIPTOR)
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

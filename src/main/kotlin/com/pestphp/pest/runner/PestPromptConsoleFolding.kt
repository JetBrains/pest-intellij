package com.pestphp.pest.runner

import com.intellij.execution.ConsoleFolding
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView

class PestPromptConsoleFolding : ConsoleFolding() {

    override fun isEnabledForConsole(consoleView: ConsoleView): Boolean {
        val context = DataManager.getInstance().getDataContext(consoleView.component)
        val descriptor = context.getData(LangDataKeys.RUN_CONTENT_DESCRIPTOR) ?: return false
        val baseConsole = descriptor.executionConsole as? BaseTestsOutputConsoleView ?: return false
        val props = baseConsole.properties
        return props is PestConsoleProperties
    }

    override fun shouldFoldLine(project: Project, line: String): Boolean {
        return line.contains(PestPressToContinueAction.PROMPT)
    }

    override fun getPlaceholderText(project: Project, lines: List<String>): String {
        return ""
    }

    override fun shouldBeAttachedToThePreviousLine(): Boolean {
        return false
    }
}

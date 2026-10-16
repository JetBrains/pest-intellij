package com.pestphp.pest.runner

import com.intellij.execution.ConsoleFolding
import com.intellij.execution.ui.ConsoleView
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ExecutionDataKeys
import com.intellij.openapi.project.Project
import com.pestphp.pest.configuration.PEST_RUN_CONFIGURATION_TYPE

class PestPromptConsoleFolding : ConsoleFolding() {

    override fun isEnabledForConsole(consoleView: ConsoleView): Boolean {
        val context = DataManager.getInstance().getDataContext(consoleView.component)
        val descriptor = context.getData(ExecutionDataKeys.RUN_CONTENT_DESCRIPTOR) ?: return false
        return descriptor.runConfigurationTypeId == PEST_RUN_CONFIGURATION_TYPE.id
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

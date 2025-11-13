package com.pestphp.pest.runner

import com.intellij.execution.ConsoleFolding
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.LangDataKeys
import com.pestphp.pest.configuration.PestRunConfigurationType

class PestPromptConsoleFolding : ConsoleFolding() {

    override fun isEnabledForConsole(consoleView: ConsoleView): Boolean {
        val context = DataManager.getInstance().getDataContext(consoleView.component)
        val descriptor = context.getData(LangDataKeys.RUN_CONTENT_DESCRIPTOR) ?: return false
        return descriptor.runConfigurationTypeId == PestRunConfigurationType.instance.id
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

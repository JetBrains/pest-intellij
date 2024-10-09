package com.pestphp.pest.features.parallel

import com.intellij.execution.Executor
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader.getDisabledIcon
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.util.text.TextWithMnemonic
import com.intellij.openapi.wm.ToolWindowId
import com.jetbrains.php.PhpIcons
import com.pestphp.pest.PestBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class PestParallelTestExecutor : Executor() {
    companion object {
        const val EXECUTOR_ID: @NonNls String = "PestParallelTestExecutor"
        const val CONTEXT_ACTION_ID: @NonNls String = "PestParallelRun"
    }

    override fun getToolWindowId(): String = ToolWindowId.RUN

    override fun getToolWindowIcon(): Icon = AllIcons.Toolwindows.ToolWindowRun

    override fun getIcon(): Icon = PhpIcons.RUN_PARA_TEST

    override fun getRerunIcon(): Icon = AllIcons.Actions.Rerun

    override fun getDisabledIcon(): Icon = getDisabledIcon(icon)

    override fun getDescription(): String = PestBundle.message("ACTION_RUN_SELECTED_CONFIGURATION_WITH_PARALLEL_DESCRIPTION")

    override fun getActionName(): String = PestBundle.message("ACTION_PEST_PARALLEL_TEXT")

    override fun getId(): String = EXECUTOR_ID

    override fun getStartActionText(): @Nls(capitalization = Nls.Capitalization.Title) String = PestBundle.message("RUN_PEST_WITH_PARALLEL")

    override fun getStartActionText(configurationName: String): String {
        val configName = if (StringUtil.isEmpty(configurationName)) "" else " '${shortenNameIfNeeded(configurationName)}'"
        return TextWithMnemonic.parse(PestBundle.message("RUN_S_WITH_PARALLEL")).replaceFirst("%s", configName).toString()
    }

    override fun getContextActionId(): String = CONTEXT_ACTION_ID

    override fun getHelpId(): String? = null
}
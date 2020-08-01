package com.pestphp.pest.configuration

import com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestIcons

class PestRunConfigurationType private constructor() :
    SimpleConfigurationType(
        "PestRunConfigurationType",
        PestBundle.message("FRAMEWORK_NAME"),
        PestBundle.message("FRAMEWORK_NAME"),
        NotNullLazyValue.createValue { PestIcons.CONFIG }
    ),
    DumbAware {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return PestRunConfiguration(project, this)
    }

    companion object {
        @JvmStatic
        val instance: PestRunConfigurationType
            get() = findConfigurationType(PestRunConfigurationType::class.java)
    }
}

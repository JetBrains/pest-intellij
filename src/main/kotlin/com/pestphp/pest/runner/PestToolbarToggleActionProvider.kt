package com.pestphp.pest.runner

import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.ToggleModelAction
import com.intellij.execution.testframework.ToggleModelActionProvider

class PestToolbarToggleActionProvider : ToggleModelActionProvider {
    override fun createToggleModelAction(properties: TestConsoleProperties): ToggleModelAction {
        return PestPressToContinueAction(properties)
    }
}

package com.pestphp.pest.runner

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.ui.ConsoleView
import com.pestphp.pest.PestBundle
import com.pestphp.pest.configuration.PestLocationProvider
import com.pestphp.pest.configuration.PestRerunFailedTestsAction

class PestConsoleProperties(config: RunConfiguration, executor: Executor) :
    SMTRunnerConsoleProperties(config, PestBundle.message("FRAMEWORK_NAME"), executor) {

    private val testLocator = PestLocationProvider(this.project)

    override fun getTestLocator(): SMTestLocator? {
        return testLocator
    }

    override fun createRerunFailedTestsAction(consoleView: ConsoleView?): AbstractRerunFailedTestsAction? {
        return consoleView?.let { PestRerunFailedTestsAction(it, this) }
    }

    override fun serviceMessageHasNewLinePrefix(): Boolean {
        return true
    }
}

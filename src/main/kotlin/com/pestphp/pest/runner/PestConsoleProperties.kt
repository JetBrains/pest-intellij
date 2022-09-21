package com.pestphp.pest.runner

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.testframework.Printer
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.execution.testframework.sm.runner.ui.TestStackTraceParser
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.pestphp.pest.PestBundle
import com.pestphp.pest.configuration.PestLocationProvider
import com.pestphp.pest.configuration.PestRerunFailedTestsAction

class PestConsoleProperties(
    config: RunConfiguration,
    executor: Executor,
    private val testLocator: PestLocationProvider
) :
    SMTRunnerConsoleProperties(config, PestBundle.message("FRAMEWORK_NAME"), executor) {

    override fun getTestLocator(): SMTestLocator {
        return testLocator
    }

    override fun createRerunFailedTestsAction(consoleView: ConsoleView?): AbstractRerunFailedTestsAction? {
        return consoleView?.let { PestRerunFailedTestsAction(it, this) }
    }

    override fun isPrintTestingStartedTime(): Boolean {
        return false
    }

    override fun printExpectedActualHeader(printer: Printer, expected: String, actual: String) {
        super.printExpectedActualHeader(printer, expected, actual)
    }

    override fun createConsole(): ConsoleView {
        return super.createConsole() as ConsoleViewImpl
    }

    override fun getTestStackTraceParser(url: String, proxy: SMTestProxy, project: Project): TestStackTraceParser {
        return object : TestStackTraceParser(url, proxy.stacktrace, proxy.errorMessage, proxy.locator, project) {

        }
    }
}

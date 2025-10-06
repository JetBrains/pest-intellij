package com.pestphp.pest.runner

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.testframework.sm.runner.MockRuntimeConfiguration
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.ide.DataManager
import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.TestActionEvent.createTestEvent
import com.intellij.util.ui.UIUtil
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.configuration.PestLocationProvider
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class PestPressToContinueActionTest : PestLightCodeFixture() {

    private class CapturingProcessHandler : NopProcessHandler() {
        val input = ByteArrayOutputStream()
        override fun getProcessInput(): OutputStream = input
    }

    private data class TestContext(
        val consoleView: SMTRunnerConsoleView,
        val processHandler: CapturingProcessHandler,
        val action: PestPressToContinueAction,
        val event: com.intellij.openapi.actionSystem.AnActionEvent,
    )

    private fun setupWithPrinted(output: String): TestContext {
        val config = MockRuntimeConfiguration(project)
        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val locator = PestLocationProvider(PhpPathMapper.create(project), project, myFixture.testDataPath)
        val props = PestConsoleProperties(config, executor, locator)

        val consoleView = SMTRunnerConsoleView(props)
        consoleView.initUI()

        val processHandler = CapturingProcessHandler()
        processHandler.startNotify()
        consoleView.attachToProcess(processHandler)

        val descriptor = RunContentDescriptor(consoleView, processHandler, consoleView.component, "Pest")

        val dataManager = DataManager.getInstance()
        (dataManager as? HeadlessDataManager)?.setTestDataProvider({ dataId ->
                                                                       if (LangDataKeys.RUN_CONTENT_DESCRIPTOR.name == dataId) descriptor else null
                                                                   }, testRootDisposable)

        val innerConsole = (consoleView.console as ConsoleViewImpl)
        innerConsole.print(output, ConsoleViewContentType.NORMAL_OUTPUT)
        innerConsole.flushDeferredText()
        UIUtil.dispatchAllInvocationEvents()

        val action = PestPressToContinueAction(props)
        val event = createTestEvent(action, DataContext.EMPTY_CONTEXT)
        action.update(event)

        return TestContext(consoleView, processHandler, action, event)
    }

    fun testWritesNewlineWhenPromptPresent() {
        val ctx = setupWithPrinted("Press any key to continue...")
        assertTrue(ctx.event.presentation.isVisible)
        assertTrue(ctx.event.presentation.isEnabled)

        ctx.action.setSelected(ctx.event, true)

        assertEquals("\n", ctx.processHandler.input.toString(Charsets.UTF_8))

        Disposer.dispose(ctx.consoleView)
    }

    fun testDisabledWhenPromptAbsent() {
        val ctx = setupWithPrinted("no prompt here")
        assertTrue(ctx.event.presentation.isVisible)
        assertFalse(ctx.event.presentation.isEnabled)

        Disposer.dispose(ctx.consoleView)
    }
}

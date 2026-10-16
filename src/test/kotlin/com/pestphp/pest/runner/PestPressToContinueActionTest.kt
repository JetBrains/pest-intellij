package com.pestphp.pest.runner

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.ExecutionDataKeys
import com.intellij.openapi.application.EDT
import com.intellij.openapi.util.Disposer
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestActionEvent.createTestEvent
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.util.ui.UIUtil
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.configuration.PestLocationProvider
import com.pestphp.pest.configuration.PEST_RUN_CONFIGURATION_TYPE
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.OutputStream

@TestApplication
class PestPressToContinueActionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
    }

    private class CapturingProcessHandler : NopProcessHandler() {
        val input = ByteArrayOutputStream()
        override fun getProcessInput(): OutputStream = input
    }

    private data class TestContext(
        val consoleView: SMTRunnerConsoleView,
        val processHandler: CapturingProcessHandler,
        val action: PestPressToContinueAction,
        val event: AnActionEvent,
    )

    private fun setupWithPrinted(output: String): TestContext {
        val project = myFixture.project
        val config = mockk<RunConfiguration>()
        every { config.project } returns project
        every { config.name } returns "Test"
        val executor = DefaultRunExecutor.getRunExecutorInstance()
        val locator = PestLocationProvider(PhpPathMapper.create(project), project, myFixture.testDataPath)
        val props = PestConsoleProperties(config, executor, locator)

        return onEdt {
            val consoleView = SMTRunnerConsoleView(props)
            consoleView.initUI()
            Disposer.register(myFixture.projectDisposable, consoleView)

            val processHandler = CapturingProcessHandler()
            processHandler.startNotify()
            consoleView.attachToProcess(processHandler)

            val descriptor = RunContentDescriptor(consoleView, processHandler, consoleView.component, "Pest")
            descriptor.runConfigurationTypeId = PEST_RUN_CONFIGURATION_TYPE.id
            Disposer.register(myFixture.projectDisposable, descriptor)

            val innerConsole = (consoleView.console as ConsoleViewImpl)
            innerConsole.print(output, ConsoleViewContentType.NORMAL_OUTPUT)
            innerConsole.flushDeferredText()
            UIUtil.dispatchAllInvocationEvents()

            val action = PestPressToContinueAction()
            val dataContext = DataContext { dataId ->
                if (ExecutionDataKeys.RUN_CONTENT_DESCRIPTOR.name == dataId) descriptor else null
            }
            val event = createTestEvent(action, dataContext)
            action.update(event)

            TestContext(consoleView, processHandler, action, event)
        }
    }

    @Test
    fun testWritesNewlineWhenPromptPresent() {
        val ctx = setupWithPrinted("Press any key to continue...")
        assertTrue(ctx.event.presentation.isVisible)
        assertTrue(ctx.event.presentation.isEnabled)

        onEdt { ctx.action.actionPerformed(ctx.event) }

        assertEquals("\n", ctx.processHandler.input.toString(Charsets.UTF_8))
    }

    @Test
    fun testDisabledWhenPromptAbsent() {
        val ctx = setupWithPrinted("no prompt here")
        assertTrue(ctx.event.presentation.isVisible)
        assertFalse(ctx.event.presentation.isEnabled)
    }

    private fun <T> onEdt(block: () -> T): T = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            block()
        }
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }
}

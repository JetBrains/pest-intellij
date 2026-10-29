package com.pestphp.pest.configuration

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.configurations.UnknownConfigurationType
import com.intellij.execution.configurations.WrappingRunConfiguration
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.TestFrameworkRunningModel
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.ui.ComponentContainer
import com.intellij.openapi.util.Disposer
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.TestDisposable
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PEST_FRAMEWORK_TYPE
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.toPestTestRegex
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JPanel

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/configuration/PestRerunFailedTestsAction")
class PestRerunFailedTestsActionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    private val executor get() = DefaultRunExecutor.getRunExecutorInstance()

    @TestDisposable
    lateinit var disposable: Disposable

    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>
    private lateinit var interpreterName: String

    @Test
    fun testFilterArgumentSelectsOneTest() {
        myFixture.configureByFile("FileWithPestTests.php")

        val (argument, regex) = runReadActionBlocking {
            val test = pestTestCall("test", "basic")
            createFailedTestsFilterArgument(listOf(test), rootPath()) to test.toPestTestRegex(rootPath())
        }

        assertEquals("--filter=/$regex/", argument)
    }

    @Test
    fun testFilterArgumentJoinsTestsWithAlternation() {
        myFixture.configureByFile("FileWithPestTests.php")

        val (argument, regexes) = runReadActionBlocking {
            val tests = listOf(pestTestCall("test", "basic"), pestTestCall("it", "is delicious"))
            createFailedTestsFilterArgument(tests, rootPath()) to tests.map { it.toPestTestRegex(rootPath()) }
        }

        assertEquals("--filter=/${regexes[0]}|${regexes[1]}/", argument)
    }

    @Test
    fun testRunProfileWrapsPestConfiguration() {
        val configuration = createConfiguration()
        val action = createAction(createConsoleProperties(configuration))

        val profile = action.getRunProfileTestAccessor(createEnvironment(configuration))

        assertInstanceOf(PestRerunProfile::class.java, profile)
        assertSame(configuration, (profile as WrappingRunConfiguration<*>).peer)
    }

    @Test
    fun testRunProfileIsNullForOtherConfiguration() {
        val configuration = createConfiguration()
        val otherConfiguration = UnknownConfigurationType.getInstance().createTemplateConfiguration(project)
        val properties = SMTRunnerConsoleProperties(otherConfiguration, "Other", executor)
        Disposer.register(disposable, properties)
        val action = createAction(properties)

        assertNull(action.getRunProfileTestAccessor(createEnvironment(configuration)))
    }

    @Test
    fun testRerunWithoutFailedTestsShowsNotification() {
        val configuration = createConfiguration()
        val notifications = collectNotifications()

        rerun(configuration, SMTestProxy.SMRootTestProxy())

        assertEquals(listOf(PestBundle.message("NO_FAILED_TESTS_FOUND")), notifications.map { it.content })
    }

    @Test
    fun testRerunWithFailedTestShowsNoNotification() {
        val configuration = createConfiguration()
        val testCall = runReadActionBlocking { pestTestCall("test", "basic") }
        val failedTest = SMTestProxy("basic", false, "pest_qn://basic")
        failedTest.locator = SMTestLocator { _, _, _, _ -> listOf(PsiLocation(testCall)) }
        val root = SMTestProxy.SMRootTestProxy()
        root.addChild(failedTest)
        failedTest.setStarted()
        failedTest.setTestFailed("failed", null, false)
        val notifications = collectNotifications()

        rerun(configuration, root)

        assertEquals(emptyList<String>(), notifications.map { it.content })
    }

    private fun rerun(configuration: PestRunConfiguration, root: SMTestProxy.SMRootTestProxy) {
        val properties = createConsoleProperties(configuration)
        val model = mockk<TestFrameworkRunningModel>()
        every { model.root } returns root
        every { model.properties } returns properties
        val action = createAction(properties)
        action.model = model
        val environment = createEnvironment(configuration)
        val profile = action.getRunProfileTestAccessor(environment)!!

        // A rerun computes the state in actionPerformed, on the EDT under the write-intent lock.
        timeoutRunBlocking(context = Dispatchers.EDT) { profile.getState(executor, environment) }
    }

    private fun createConsoleProperties(configuration: PestRunConfiguration): SMTRunnerConsoleProperties =
        configuration.createTestConsoleProperties(executor).also { Disposer.register(disposable, it) }

    private fun collectNotifications(): List<Notification> {
        val notifications = CopyOnWriteArrayList<Notification>()
        project.messageBus.connect(disposable).subscribe(Notifications.TOPIC, object : Notifications {
            override fun notify(notification: Notification) {
                notifications += notification
            }
        })
        return notifications
    }

    private fun createAction(properties: SMTRunnerConsoleProperties): PestRerunFailedTestsAction {
        val componentContainer = mockk<ComponentContainer>()
        every { componentContainer.component } returns JPanel()
        return PestRerunFailedTestsAction(componentContainer, properties)
    }

    private fun createEnvironment(configuration: PestRunConfiguration): ExecutionEnvironment =
        ExecutionEnvironmentBuilder(project, executor)
            .runProfile(configuration)
            .runner(mockk<ProgramRunner<*>>(relaxed = true))
            .build()

    private fun createConfiguration(): PestRunConfiguration {
        val file = myFixture.configureByFile("FileWithPestTests.php")
        val elementAtCaret = runReadActionBlocking { file.findElementAt(myFixture.editor.caretModel.offset) }

        PestTestUtil.createPestFrameworkConfiguration(project)

        val configuration = runReadActionBlocking {
            PestRunConfigurationProducer().createConfigurationFromContext(
                ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement(elementAtCaret))
            )?.configuration as PestRunConfiguration
        }
        configuration.settings.commandLineSettings.interpreterSettings.interpreterName = interpreterName
        return configuration
    }

    private fun pestTestCall(functionName: String, description: String): FunctionReferenceImpl =
        PsiTreeUtil.findChildrenOfType(myFixture.file, FunctionReferenceImpl::class.java).single {
            it.name == functionName && (it.getParameter(0) as? StringLiteralExpression)?.contents == description
        }

    private fun rootPath(): String = myFixture.file.virtualFile.parent.path

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/configuration/PestRerunFailedTestsAction")
        interpreterName = PlatformTestUtil.getTestName(testInfo.testMethod.get().name, false)
        val interpreter = PhpInterpreter().apply {
            name = interpreterName
            homePath = "${PestTestUtil.getTestDataPath()}/configuration/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PEST_FRAMEWORK_TYPE)
    }

    @AfterEach
    fun tearDown() {
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PEST_FRAMEWORK_TYPE, configurationsBackup)
        PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
    }
}

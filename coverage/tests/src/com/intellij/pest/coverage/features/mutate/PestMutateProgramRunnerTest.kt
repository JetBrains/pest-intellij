package com.intellij.pest.coverage.features.mutate

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiElement
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/coverage/tests/testData/features/mutate")
class PestMutateProgramRunnerTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>
    private lateinit var testMethodName: String

    private fun getTestDataPath(): String {
        val intellijPath = Path.of(IdeaTestExecutionPolicy.getHomePathWithPolicy(), "phpstorm/pest/coverage/tests/testData/features/mutate")
        return if (intellijPath.exists()) {
            intellijPath.pathString
        } else {
            "testData/features/mutate"
        }
    }

    @Test
    fun testCannotRunWrongExecutorId(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertFalse(PestMutateProgramRunner().canRun(PestMutateTestExecutor.EXECUTOR_ID + "1", configuration))
    }

    @Test
    fun testCanRunFile(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertTrue(PestMutateProgramRunner().canRun(PestMutateTestExecutor.EXECUTOR_ID, configuration))
    }

    @Test
    fun testCanRunFunction(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.firstChild?.lastChild?.firstChild } ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestMutateProgramRunner().canRun(PestMutateTestExecutor.EXECUTOR_ID, configuration))
    }

    @Test
    fun testCanRunDirectory(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.containingDirectory } ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestMutateProgramRunner().canRun(PestMutateTestExecutor.EXECUTOR_ID, configuration))
    }

    private fun createConfiguration(psiElement: PsiElement): PestRunConfiguration {
        PestTestUtil.createPestFrameworkConfiguration(project)
        val runConfiguration = runReadActionBlocking {
            val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement(psiElement))
            PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration
                as? PestRunConfiguration
        }
        runConfiguration!!.settings.commandLineSettings.interpreterSettings.interpreterName = PlatformTestUtil.getTestName(testMethodName, false)
        return runConfiguration
    }

    private fun doTest(block: () -> Unit) {
        myFixture.configureByFile("ATest.php")
        block()
    }

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, getTestDataPath())
        testMethodName = testInfo.testMethod.get().name
        val interpreter = PhpInterpreter().apply {
            name = PlatformTestUtil.getTestName(testMethodName, false)
            homePath = "${myFixture.testDataPath}/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
    }

    @AfterEach
    fun tearDown() {
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, configurationsBackup)
        PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
    }
}
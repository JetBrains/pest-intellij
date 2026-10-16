package com.intellij.pest.coverage

import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageHelper
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
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer
import com.pestphp.pest.PEST_FRAMEWORK_TYPE
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/coverage/tests/testData")
class PestCoverageProgramRunnerTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>
    private lateinit var testMethodName: String

    private fun getTestDataPath(): String {
        val intellijPath = Path.of(IdeaTestExecutionPolicy.getHomePathWithPolicy(), "phpstorm/pest/coverage/tests/testData")
        return if (intellijPath.exists()) {
            intellijPath.pathString
        } else {
            "testData"
        }
    }

    @Test
    fun testCannotRunWrongExecutorId(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertFalse(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID + "1", configuration))
    }

    @Test
    fun testCanRunFile(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertTrue(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID, configuration))
    }

    @Test
    fun testCanRunFunction(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.firstChild?.lastChild?.firstChild } ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID, configuration))
    }

    @Test
    fun testCanRunDirectory(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.containingDirectory } ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID, configuration))
    }

    @Test
    fun testBuildFile(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)

        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity ${configuredFilePath()}"
        assertEquals(expected, pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testBuildFunction(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.firstChild?.lastChild?.firstChild } ?: return@doTest
        val configuration = createConfiguration(testElement)

        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity ${configuredFilePath()}"
        assertEquals(
            expected,
            pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list
                .joinToString(" ")
                .substringBefore(" --filter")
        )
    }

    @Test
    fun testBuildDirectory(): Unit = doTest {
        val testElement = runReadActionBlocking { myFixture.file?.containingDirectory } ?: return@doTest
        val configuration = createConfiguration(testElement)
        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity ${configuredFilePath().substringBeforeLast('/')}"
        assertEquals(expected, pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testBuildFileWithEnabledParallelTesting(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        configuration.pestSettings.pestRunnerSettings.parallelTestingEnabled = true

        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity ${configuredFilePath()} --parallel --log-teamcity php://stdout"
        assertEquals(expected, pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testCreateCoverageSuiteOnRunningCoverageTests(): Unit = doTest {
        val configuration = createConfiguration(myFixture.file)
        CoverageHelper.resetCoverageSuit(configuration)
        assertEquals(1, CoverageDataManager.getInstance(project).getSuites().size)
    }

    private fun createConfiguration(psiElement: PsiElement): PestRunConfiguration {
        PestTestUtil.createPestFrameworkConfiguration(project)
        val runConfiguration = runReadActionBlocking {
            val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement(psiElement))
            PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration as? PestRunConfiguration
        }
        runConfiguration!!.settings.commandLineSettings.interpreterSettings.interpreterName = PlatformTestUtil.getTestName(testMethodName, false)
        return runConfiguration
    }

    private fun configuredFilePath(): String = runReadActionBlocking { myFixture.file.virtualFile.path }

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
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PEST_FRAMEWORK_TYPE)
    }

    @AfterEach
    fun tearDown() {
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PEST_FRAMEWORK_TYPE, configurationsBackup)
        PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
    }
}
package com.pestphp.pest.configuration

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.PEST_FRAMEWORK_TYPE
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

@TestApplication
class PestRunConfigurationTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>
    private lateinit var testMethodName: String

    @Test
    fun testRunConfigurationRunnerSettingsIsPestRunnerSettings() {
        val configuration = createConfiguration()

        assertNotNull(configuration)
        assertInstanceOf(PestRunConfigurationSettings::class.java, configuration.settings)
        assertInstanceOf(PestRunnerSettings::class.java, configuration.settings.runnerSettings)
    }

    @Test
    fun testRunConfigurationClone() {
        val configuration = createConfiguration()
        configuration.pestSettings.pestRunnerSettings.coverageEngine = PhpUnitCoverageEngine.CoverageEngine.PCOV
        configuration.pestSettings.pestRunnerSettings.parallelTestingEnabled = true

        val clone = configuration.clone() as PestRunConfiguration
        assertEquals(PhpUnitCoverageEngine.CoverageEngine.PCOV, clone.pestSettings.pestRunnerSettings.coverageEngine)
        assertEquals(true, clone.pestSettings.pestRunnerSettings.parallelTestingEnabled)
    }

    @Test
    fun testRunConfigurationCommand() {
        val command = createCommand()
        val expectedCommand = "randomPath --teamcity ${configuredFilePath()}"
        assertEquals(expectedCommand, command.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    @Test
    fun testRunConfigurationCommandWithEnabledParallelTesting() {
        val configuration = createConfiguration()
        configuration.pestSettings.pestRunnerSettings.parallelTestingEnabled = true
        val command = configuration.createCommand(configuration.interpreter!!, mutableMapOf(), mutableListOf(), false)

        val expectedCommand = "randomPath --teamcity ${configuredFilePath()} --parallel --log-teamcity php://stdout"
        assertEquals(expectedCommand, command.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    private fun createCommand(): PhpCommandSettings {
        val configuration = createConfiguration()

        return configuration.createCommand(configuration.interpreter!!, mutableMapOf(), mutableListOf(), false)
    }

    @Test
    fun testDescribeBlockActionNameIsCleanWhileFilterKeepsRawName() {
        val configuration = createConfiguration("FileWithDescribeBlock.php")
        val runner = configuration.settings.runnerSettings

        assertEquals("`my block` → ", runner.methodName)

        assertEquals("my block", configuration.actionName)
        assertEquals("FileWithDescribeBlock.php::my block", configuration.suggestedName())
    }

    private fun createConfiguration(fileName: String = "FileWithPestTest.php"): PestRunConfiguration {
        val file = myFixture.configureByFile(fileName)
        val elementAtCaret = runReadActionBlocking { file.findElementAt(myFixture.editor.caretModel.offset) }

        PestTestUtil.createPestFrameworkConfiguration(project)

        val configuration = runReadActionBlocking {
            PestRunConfigurationProducer().createConfigurationFromContext(
                ConfigurationContext.createEmptyContextForLocation(
                    PsiLocation.fromPsiElement(elementAtCaret)
                )
            )?.configuration as PestRunConfiguration
        }
        configuration.settings.commandLineSettings.interpreterSettings.interpreterName = PlatformTestUtil.getTestName(testMethodName, false)
        return configuration
    }

    private fun configuredFilePath(): String = runReadActionBlocking { myFixture.file.virtualFile.path }

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/configuration")
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

package com.pestphp.pest.configuration

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.phpunit.coverage.PhpUnitCoverageEngine
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestLightCodeFixture

class PestRunConfigurationTest : PestLightCodeFixture() {
    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>

    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/configuration"
    }

    fun testRunConfigurationRunnerSettingsIsPestRunnerSettings() {
        val configuration = createConfiguration()

        assertNotNull(configuration)
        assertInstanceOf(configuration.settings, PestRunConfigurationSettings::class.java)
        assertInstanceOf(configuration.settings.runnerSettings, PestRunnerSettings::class.java)
    }

    fun testRunConfigurationClone() {
        val configuration = createConfiguration()
        configuration.pestSettings.pestRunnerSettings.coverageEngine = PhpUnitCoverageEngine.CoverageEngine.PCOV
        configuration.pestSettings.pestRunnerSettings.parallelTestingEnabled = true

        val clone = configuration.clone() as PestRunConfiguration
        assertEquals(PhpUnitCoverageEngine.CoverageEngine.PCOV, clone.pestSettings.pestRunnerSettings.coverageEngine)
        assertEquals(true, clone.pestSettings.pestRunnerSettings.parallelTestingEnabled)
    }

    fun testRunConfigurationCommand() {
        val command = createCommand()
        val expectedCommand = "randomPath --teamcity /src/FileWithPestTest.php"
        assertEquals(expectedCommand, command.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testRunConfigurationCommandWithEnabledParallelTesting() {
        val configuration = createConfiguration()
        configuration.pestSettings.pestRunnerSettings.parallelTestingEnabled = true
        val command = configuration.createCommand(configuration.interpreter!!, mutableMapOf(), mutableListOf(), false)

        val expectedCommand = "randomPath --teamcity /src/FileWithPestTest.php --parallel --log-teamcity php://stdout"
        assertEquals(expectedCommand, command.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    private fun createCommand(): PhpCommandSettings {
        val configuration = createConfiguration()

        return configuration.createCommand(configuration.interpreter!!, mutableMapOf(), mutableListOf(), false)
    }

    private fun createConfiguration(): PestRunConfiguration {
        val file = myFixture.configureByFile("FileWithPestTest.php")
        val elementAtCaret = file.findElementAt(myFixture.editor.caretModel.offset)

        createPestFrameworkConfiguration()

        val configuration = PestRunConfigurationProducer().createConfigurationFromContext(
            ConfigurationContext.createEmptyContextForLocation(
                PsiLocation.fromPsiElement(elementAtCaret)
            )
        )?.configuration as PestRunConfiguration
        configuration.settings.commandLineSettings.interpreterSettings.interpreterName = getTestName(false)
        return configuration
    }

    override fun setUp() {
        super.setUp()
        val interpreter = PhpInterpreter().apply {
            name = getTestName(false)
            homePath = "$testDataPath/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
    }

    override fun tearDown() {
        try {
            PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, configurationsBackup)
            PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
        } catch (e: Throwable) {
            addSuppressedException(e)
        } finally {
            super.tearDown()
        }
    }
}

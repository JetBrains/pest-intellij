package com.intellij.pest.coverage

import com.intellij.coverage.CoverageDataManager
import com.intellij.coverage.CoverageHelper
import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

@TestDataPath($$"$CONTENT_ROOT/testData")
class PestCoverageProgramRunnerTest : PestLightCodeFixture() {
    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>

    override fun getTestDataPath(): String {
        val intellijPath = Path.of(IdeaTestExecutionPolicy.getHomePathWithPolicy(), "phpstorm/pest/coverage/tests/testData")
        return if (intellijPath.exists()) {
            intellijPath.pathString
        } else {
            "testData"
        }
    }

    fun testCannotRunWrongExecutorId() = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertFalse(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID + "1", configuration))
    }

    fun testCanRunFile() = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertTrue(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID, configuration))
    }

    fun testCanRunFunction() = doTest {
        val testElement = myFixture.file?.firstChild?.lastChild?.firstChild ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID, configuration))
    }

    fun testCanRunDirectory() = doTest {
        val testElement = myFixture.file?.containingDirectory ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestCoverageProgramRunner().canRun(PestCoverageProgramRunner.EXECUTOR_ID, configuration))
    }

    fun testBuildFile() = doTest {
        val configuration = createConfiguration(myFixture.file)

        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity /src/ATest.php"
        assertEquals(expected, pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildFunction() = doTest {
        val testElement = myFixture.file?.firstChild?.lastChild?.firstChild ?: return@doTest
        val configuration = createConfiguration(testElement)

        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity /src/ATest.php"
        assertEquals(
            expected,
            pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list
                .joinToString(" ")
                .substringBefore(" --filter")
        )
    }

    fun testBuildDirectory() = doTest {
        val testElement = myFixture.file?.containingDirectory ?: return@doTest
        val configuration = createConfiguration(testElement)
        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity /src"
        assertEquals(expected, pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildFileWithEnabledParallelTesting() = doTest {
        val configuration = createConfiguration(myFixture.file)
        configuration.pestSettings.pestRunnerSettings.parallelTestingEnabled = true

        val pestCoverageCommandSettings = PestCoverageProgramRunner().createPestCoverageCommand(configuration, configuration.interpreter!!, emptyList(), "", "")

        val expected = "-dxdebug.coverage_enable=1 -dxdebug.mode=coverage randomPath --teamcity /src/ATest.php --parallel --log-teamcity php://stdout"
        assertEquals(expected, pestCoverageCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testCreateCoverageSuiteOnRunningCoverageTests() = doTest {
        val configuration = createConfiguration(myFixture.file)
        CoverageHelper.resetCoverageSuit(configuration)
        assertSize(1, CoverageDataManager.getInstance(project).getSuites())
    }

    private fun createConfiguration(psiElement: PsiElement): PestRunConfiguration {
        createPestFrameworkConfiguration()
        val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement(psiElement))
        val runConfiguration = PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration as? PestRunConfiguration
        runConfiguration!!.settings.commandLineSettings.interpreterSettings.interpreterName = getTestName(false)
        return runConfiguration
    }

    private fun doTest(block: () -> Unit) {
        myFixture.configureByFile("ATest.php")
        block()
    }

    override fun setUp() {
        super.setUp()
        val interpreter = PhpInterpreter().apply {
            name = getTestName(false)
            homePath = "$testDataPath/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.Companion.instance)
    }

    override fun tearDown() {
        try {
            PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.Companion.instance, configurationsBackup)
            PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
        } catch (e: Throwable) {
            addSuppressedException(e)
        } finally {
            super.tearDown()
        }
    }
}
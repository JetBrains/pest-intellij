package com.pestphp.pest.features.parallel

import com.intellij.execution.ExecutionException
import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer
import com.pestphp.pest.configuration.PestVersionDetector
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject

@TestDataPath("/com/pestphp/pest/features/parallel")
class PestParallelProgramRunnerTest : PestLightCodeFixture() {
    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>
    private lateinit var mockDetector: PestVersionDetector

    override fun getTestDataPath(): String = "src/test/resources/com/pestphp/pest/features/parallel"

    fun testCannotRunWrongExecutorId() = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertFalse(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID + "1", configuration))
    }

    fun testCanRunFile() = doTest {
        val configuration = createConfiguration(myFixture.file)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    fun testCanRunFunction() = doTest {
        val testElement = myFixture.file?.firstChild?.lastChild?.firstChild ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    fun testCanRunDirectory() = doTest {
        val testElement = myFixture.file?.containingDirectory ?: return@doTest
        val configuration = createConfiguration(testElement)
        assertTrue(PestParallelProgramRunner().canRun(PestParallelTestExecutor.EXECUTOR_ID, configuration))
    }

    fun testBuildFile() = doTest {
        val configuration = createConfiguration(myFixture.file)

        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src/ATest.php"
        assertEquals(expected, pestParallelCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildFunction() = doTest {
        val testElement = myFixture.file?.firstChild?.lastChild?.firstChild ?: return@doTest
        val configuration = createConfiguration(testElement)

        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src/ATest.php"
        assertEquals(
            expected,
            pestParallelCommandSettings.createGeneralCommandLine().parametersList.list
                .joinToString(" ")
                .substringBefore(" --filter")
        )
    }

    fun testBuildDirectory() = doTest {
        val testElement = myFixture.file?.containingDirectory ?: return@doTest
        val configuration = createConfiguration(testElement)
        val pestParallelCommandSettings = createPestParallelCommand(configuration)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src"
        assertEquals(expected, pestParallelCommandSettings.createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildWithParaTestModeWhenVersionAtLeast4_6_0() = doTest {
        every { mockDetector.getVersion(any(), any(), any()) } returns "4.6.0"
        val configuration = createConfiguration(myFixture.file)

        val expected = "randomPath --teamcity --parallel /src/ATest.php"
        assertEquals(expected, createPestParallelCommand(configuration).createGeneralCommandLine().parametersList.list.joinToString(" "))
    }

    fun testBuildWithLegacyModeWhenVersionBelow4_6_0() = doTest {
        every { mockDetector.getVersion(any(), any(), any()) } returns "4.5.0"
        val configuration = createConfiguration(myFixture.file)

        val expected = "randomPath --teamcity --parallel --log-teamcity php://stdout /src/ATest.php"
        assertEquals(expected, createPestParallelCommand(configuration).createGeneralCommandLine().parametersList.list.joinToString(" "))
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
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
        PhpTestFrameworkSettingsManager.getInstance(project)
            .getOrCreateByInterpreter(PestFrameworkType.instance, interpreter, null, false)
            ?.executablePath = "randomPath"
        mockDetector = mockk()
        mockkObject(PestVersionDetector.Companion)
        every { PestVersionDetector.instance } returns mockDetector
        every { mockDetector.getVersion(any(), any(), any()) } throws ExecutionException("not available in tests")
    }

    override fun tearDown() {
        try {
            PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, configurationsBackup)
            PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
            unmockkObject(PestVersionDetector.Companion)
        } catch (e: Throwable) {
            addSuppressedException(e)
        } finally {
            super.tearDown()
        }
    }
}
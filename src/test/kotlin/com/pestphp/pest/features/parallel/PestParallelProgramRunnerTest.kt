package com.pestphp.pest.features.parallel

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer

@TestDataPath("/com/pestphp/pest/features/parallel")
class PestParallelProgramRunnerTest : PestLightCodeFixture() {
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

    private fun createConfiguration(psiElement: PsiElement): PestRunConfiguration {
        createPestFrameworkConfiguration()
        val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement(psiElement))
        val configuration = PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration as? PestRunConfiguration
        configuration!!.settings.commandLineSettings.interpreterSettings.interpreterName = getTestName(false)
        return configuration
    }

    private fun doTest(block: () -> Unit) {
        val file = myFixture.configureByFile("ATest.php")
        myFixture.openFileInEditor(file.virtualFile)
        block()
    }

    override fun setUp() {
        super.setUp()
        val interpreter = PhpInterpreter().apply {
            name = getTestName(false)
            homePath = "$testDataPath/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
    }

    override fun tearDown() {
        try {
            PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
        } catch (e: Throwable) {
            addSuppressedException(e)
        } finally {
            super.tearDown()
        }
    }
}
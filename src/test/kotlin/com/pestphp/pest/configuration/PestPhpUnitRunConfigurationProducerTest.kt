package com.pestphp.pest.configuration

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiFile
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

@TestApplication
class PestPhpUnitRunConfigurationProducerTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>

    @Test
    fun testFileScopeFromPhpUnitFileWhenPestEnabled() {
        PestTestUtil.createPestFrameworkConfiguration(project)
        val configuration = configureAndProduce("FileWithPhpUnitTestAtFileScope.php")

        assertNotNull(configuration, "Pest producer must offer a configuration for PHPUnit-style files when Pest is enabled")
        assertEquals(PhpTestRunnerSettings.Scope.File, configuration!!.settings.runnerSettings.scope)
        assertTrue(
            configuration.settings.runnerSettings.filePath?.endsWith("FileWithPhpUnitTestAtFileScope.php") == true,
            "File path should point to the configured PHPUnit-style file"
        )
    }

    @Test
    fun testPhpUnitTestMethodFallsBackToFileScope() {
        // Method-scope filtering for PHPUnit-style cases is intentionally left to PHPUnit's own
        // producer. A click inside a PHPUnit method still offers a Pest configuration, but as a
        // file-scope run so Pest's auto-discovery handles it.
        PestTestUtil.createPestFrameworkConfiguration(project)
        val configuration = configureAndProduce("FileWithPhpUnitTest.php")

        assertNotNull(configuration, "Pest producer must offer a configuration for a PHPUnit-style file when Pest is enabled")
        assertEquals(PhpTestRunnerSettings.Scope.File, configuration!!.settings.runnerSettings.scope)
        assertTrue(
            configuration.settings.runnerSettings.filePath?.endsWith("FileWithPhpUnitTest.php") == true,
            "File path should point to the PHPUnit-style file"
        )
    }

    @Test
    fun testNoPestConfigForPhpUnitFileWhenPestDisabled() {
        val configuration = configureAndProduce("FileWithPhpUnitTest.php")

        assertNull(configuration, "Pest producer must not offer a configuration when Pest is not configured")
    }

    private fun configureAndProduce(fileName: String): PestRunConfiguration? {
        val file: PsiFile = myFixture.configureByFile(fileName)
        return runReadActionBlocking {
            val elementAtCaret = file.findElementAt(myFixture.editor.caretModel.offset)

            val fromContext = PestRunConfigurationProducer().createConfigurationFromContext(
                ConfigurationContext.createEmptyContextForLocation(
                    PsiLocation.fromPsiElement(elementAtCaret)
                )
            ) ?: return@runReadActionBlocking null

            fromContext.configuration as? PestRunConfiguration
        }
    }

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/configuration")
        val interpreter = PhpInterpreter().apply {
            name = PlatformTestUtil.getTestName(testInfo.testMethod.get().name, false)
            homePath = "${myFixture.testDataPath}/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, emptyList())
    }

    @AfterEach
    fun tearDown() {
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, configurationsBackup)
        PhpInterpretersManagerImpl.getInstance(project).interpreters = emptyList()
    }
}

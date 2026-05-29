package com.pestphp.pest.configuration

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.PsiFile
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.jetbrains.php.testFramework.run.PhpTestRunnerSettings
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.PestLightCodeFixture

class PestPhpUnitRunConfigurationProducerTest : PestLightCodeFixture() {
    private lateinit var configurationsBackup: List<PhpTestFrameworkConfiguration>

    override fun getBasePath(): String = "${super.getBasePath()}/configuration"

    fun testFileScopeFromPhpUnitFileWhenPestEnabled() {
        createPestFrameworkConfiguration()
        val configuration = configureAndProduce("FileWithPhpUnitTestAtFileScope.php")

        assertNotNull("Pest producer must offer a configuration for PHPUnit-style files when Pest is enabled", configuration)
        assertEquals(PhpTestRunnerSettings.Scope.File, configuration!!.settings.runnerSettings.scope)
        assertTrue(
            "File path should point to the configured PHPUnit-style file",
            configuration.settings.runnerSettings.filePath?.endsWith("FileWithPhpUnitTestAtFileScope.php") == true
        )
    }

    fun testPhpUnitTestMethodFallsBackToFileScope() {
        // Method-scope filtering for PHPUnit-style cases is intentionally left to PHPUnit's own
        // producer. A click inside a PHPUnit method still offers a Pest configuration, but as a
        // file-scope run so Pest's auto-discovery handles it.
        createPestFrameworkConfiguration()
        val configuration = configureAndProduce("FileWithPhpUnitTest.php")

        assertNotNull("Pest producer must offer a configuration for a PHPUnit-style file when Pest is enabled", configuration)
        assertEquals(PhpTestRunnerSettings.Scope.File, configuration!!.settings.runnerSettings.scope)
        assertTrue(
            "File path should point to the PHPUnit-style file",
            configuration.settings.runnerSettings.filePath?.endsWith("FileWithPhpUnitTest.php") == true
        )
    }

    fun testNoPestConfigForPhpUnitFileWhenPestDisabled() {
        val configuration = configureAndProduce("FileWithPhpUnitTest.php")

        assertNull("Pest producer must not offer a configuration when Pest is not configured", configuration)
    }

    private fun configureAndProduce(fileName: String): PestRunConfiguration? {
        val file: PsiFile = myFixture.configureByFile(fileName)
        val elementAtCaret = file.findElementAt(myFixture.editor.caretModel.offset)

        val fromContext = PestRunConfigurationProducer().createConfigurationFromContext(
            ConfigurationContext.createEmptyContextForLocation(
                PsiLocation.fromPsiElement(elementAtCaret)
            )
        ) ?: return null

        return fromContext.configuration as? PestRunConfiguration
    }

    override fun setUp() {
        super.setUp()
        val interpreter = PhpInterpreter().apply {
            name = getTestName(false)
            homePath = "$testDataPath/php"
        }
        PhpInterpretersManagerImpl.getInstance(project).addInterpreter(interpreter)
        configurationsBackup = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, emptyList())
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

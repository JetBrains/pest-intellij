package com.pestphp.pest.configuration

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.pestphp.pest.tests.PestLightCodeFixture


class PestRunConfigurationTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
        return "src/test/resources/com/pestphp/pest/configuration"
    }

    fun testRunConfigurationRunnerSettingsIsPestRunnerSettings() {
        val file = myFixture.configureByFile("FileWithPestTest.php")
        val elementAtCaret = file.findElementAt(myFixture.editor.caretModel.offset)

        createPestFrameworkConfiguration()

        val configuration = PestRunConfigurationProducer().createConfigurationFromContext(
            ConfigurationContext.createEmptyContextForLocation(
                PsiLocation.fromPsiElement(elementAtCaret)
            )
        )!!.configuration as? PestRunConfiguration

        assertNotNull(configuration)
        assertTrue(configuration!!.settings is PestRunConfigurationSettings)
        assertTrue(configuration.settings.runnerSettings is PestRunnerSettings)
    }
}
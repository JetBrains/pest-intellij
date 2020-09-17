package com.pestphp.pest.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType

abstract class PestLightCodeFixture : BasePlatformTestCase() {
    override fun getBasePath() = "src/test/kotlin/com/pestphp/pest/tests/"

    protected fun assertCompletion(vararg shouldContain: String) {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings ?: return fail("empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun createPestFrameworkConfiguration(): PhpTestFrameworkConfiguration {
        val configuration = PhpTestFrameworkSettingsManager
            .getInstance(myFixture.project)
            .getOrCreateByInterpreter(PestFrameworkType.instance, PhpInterpreter())!!
        configuration.executablePath = "randomPath"

        return configuration
    }
}

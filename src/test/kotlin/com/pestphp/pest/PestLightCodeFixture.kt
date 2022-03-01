package com.pestphp.pest

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager

@Suppress("UnnecessaryAbstractClass")
@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest")
abstract class PestLightCodeFixture : BasePlatformTestCase() {
    override fun getBasePath() = "src/test/resources/com/pestphp/pest"

    protected fun assertCompletion(vararg shouldContain: String) {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings ?: return fail("empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun assertNoCompletion() {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings

        assertNullOrEmpty(strings)
    }

    protected fun createPestFrameworkConfiguration(): PhpTestFrameworkConfiguration? {
        val configuration = PhpTestFrameworkSettingsManager
            .getInstance(myFixture.project)
            .getOrCreateByInterpreter(PestFrameworkType.instance, PhpInterpreter())
        configuration?.executablePath = "randomPath"

        return configuration
    }

    protected fun configureByPhpCode(code: String) {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $code")
    }
}

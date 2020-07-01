package com.pestphp.pest.tests

import com.intellij.testFramework.fixtures.BasePlatformTestCase

abstract class PestLightCodeFixture: BasePlatformTestCase() {
    override fun getBasePath() = "src/test/kotlin/com/pestphp/pest/tests/"

    protected fun assertCompletion(vararg shouldContain: String) {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings ?: return fail("empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }
}
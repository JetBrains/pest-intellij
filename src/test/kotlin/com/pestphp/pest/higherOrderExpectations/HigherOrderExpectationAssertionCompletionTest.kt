package com.pestphp.pest.higherOrderExpectations

import com.pestphp.pest.PestLightCodeFixture

class HigherOrderExpectationAssertionCompletionTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/higherOrderExpectations"
    }

    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("stubs.php")
    }

    fun testFieldCompletion() {
        myFixture.configureByFile(
            "ExpectPropertyAssertionCompletion.php"
        )

        assertCompletion("toBeTrue", "toEqual")
    }

    fun testMethodCompletion() {
        myFixture.configureByFile(
            "ExpectMethodAssertionCompletion.php"
        )

        assertCompletion("toBeTrue", "toEqual")
    }

    fun testMethodCompletionChained() {
        myFixture.configureByFile(
            "ExpectMethodAssertionCompletionChained.php"
        )

        assertCompletion("toBeInt")
    }

    fun testFieldCompletionChained() {
        myFixture.configureByFile(
            "ExpectPropertyAssertionCompletionChained.php"
        )

        assertCompletion("toBeInt")
    }

    fun testMethodCompletionChainedAssertions() {
        myFixture.configureByFile(
            "ExpectMethodAssertionCompletionChainedAssertions.php"
        )

        assertCompletion("toBeInt")
    }

    fun testFieldCompletionChainedAssertions() {
        myFixture.configureByFile(
            "ExpectPropertyAssertionCompletionChainedAssertions.php"
        )

        assertCompletion("toBeInt")
    }
}
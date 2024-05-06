package com.pestphp.pest.higherOrderExpectations

import com.pestphp.pest.PestLightCodeFixture

class HigherOrderExpectationCompletionTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/higherOrderExpectations"
    }

    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject(".phpstorm.meta.php")
    }

    fun testFieldCompletion() {
        myFixture.configureByFile(
            "ExpectPropertyCompletion.php"
        )

        assertCompletion("otherExample", "test")
    }

    fun testMethodCompletion() {
        myFixture.configureByFile(
            "ExpectMethodCompletion.php"
        )

        assertCompletion("getOtherExample", "getTest")
    }

    fun testMethodCompletionChained() {
        myFixture.configureByFile(
            "ExpectMethodCompletionChained.php"
        )

        assertCompletion("getTimestamp");
    }

    fun testFieldCompletionChained() {
        myFixture.configureByFile(
            "ExpectPropertyCompletionChained.php"
        )

        assertCompletion("getTimestamp")
    }

    fun testChainedAssertionsFieldCompletion() {
        myFixture.configureByFile(
            "ExpectPropertyCompletionChainedAssertions.php"
        )

        assertCompletion("otherExample", "test")
    }

    fun testChainedAssertionsMethodCompletion() {
        myFixture.configureByFile(
            "ExpectMethodCompletionChainedAssertions.php"
        )

        assertCompletion("getOtherExample", "getTest")
    }
}
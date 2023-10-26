package com.pestphp.pest.higherOrderExpectations

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/higherOrderExpectations")
class HigherOrderExpectationCompletionTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/higherOrderExpectations"
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

    fun testMethodCompletionDeeplyChained() {
        myFixture.configureByFile(
            "ExpectMethodCompletionDeeplyChained.php"
        )

        assertCompletion("getTimestamp");
    }
}
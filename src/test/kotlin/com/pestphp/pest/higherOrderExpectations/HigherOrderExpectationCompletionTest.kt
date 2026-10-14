package com.pestphp.pest.higherOrderExpectations

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class HigherOrderExpectationCompletionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/higherOrderExpectations")
        myFixture.copyFileToProject(".phpstorm.meta.php")
    }

    @Test
    fun testFieldCompletion() {
        myFixture.configureByFile(
            "ExpectPropertyCompletion.php"
        )

        PestTestUtil.assertCompletion(myFixture, "otherExample", "test")
    }

    @Test
    fun testMethodCompletion() {
        myFixture.configureByFile(
            "ExpectMethodCompletion.php"
        )

        PestTestUtil.assertCompletion(myFixture, "getOtherExample", "getTest")
    }

    @Test
    fun testMethodCompletionChained() {
        myFixture.configureByFile(
            "ExpectMethodCompletionChained.php"
        )

        PestTestUtil.assertCompletion(myFixture, "getTimestamp")
    }

    @Test
    fun testFieldCompletionChained() {
        myFixture.configureByFile(
            "ExpectPropertyCompletionChained.php"
        )

        PestTestUtil.assertCompletion(myFixture, "getTimestamp")
    }

    @Test
    fun testChainedAssertionsFieldCompletion() {
        myFixture.configureByFile(
            "ExpectPropertyCompletionChainedAssertions.php"
        )

        PestTestUtil.assertCompletion(myFixture, "otherExample", "test")
    }

    @Test
    fun testChainedAssertionsMethodCompletion() {
        myFixture.configureByFile(
            "ExpectMethodCompletionChainedAssertions.php"
        )

        PestTestUtil.assertCompletion(myFixture, "getOtherExample", "getTest")
    }
}
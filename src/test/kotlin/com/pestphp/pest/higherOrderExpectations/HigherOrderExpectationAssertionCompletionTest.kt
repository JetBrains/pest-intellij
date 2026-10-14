package com.pestphp.pest.higherOrderExpectations

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class HigherOrderExpectationAssertionCompletionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/higherOrderExpectations")
        myFixture.copyFileToProject("stubs.php")
    }

    @Test
    fun testFieldCompletion() {
        myFixture.configureByFile(
            "ExpectPropertyAssertionCompletion.php"
        )

        PestTestUtil.assertCompletion(myFixture, "toBeTrue", "toEqual")
    }

    @Test
    fun testMethodCompletion() {
        myFixture.configureByFile(
            "ExpectMethodAssertionCompletion.php"
        )

        PestTestUtil.assertCompletion(myFixture, "toBeTrue", "toEqual")
    }

    @Test
    fun testMethodCompletionChained() {
        myFixture.configureByFile(
            "ExpectMethodAssertionCompletionChained.php"
        )

        PestTestUtil.assertCompletion(myFixture, "toBeInt")
    }

    @Test
    fun testFieldCompletionChained() {
        myFixture.configureByFile(
            "ExpectPropertyAssertionCompletionChained.php"
        )

        PestTestUtil.assertCompletion(myFixture, "toBeInt")
    }

    @Test
    fun testMethodCompletionChainedAssertions() {
        myFixture.configureByFile(
            "ExpectMethodAssertionCompletionChainedAssertions.php"
        )

        PestTestUtil.assertCompletion(myFixture, "toBeInt")
    }

    @Test
    fun testFieldCompletionChainedAssertions() {
        myFixture.configureByFile(
            "ExpectPropertyAssertionCompletionChainedAssertions.php"
        )

        PestTestUtil.assertCompletion(myFixture, "toBeInt")
    }
}
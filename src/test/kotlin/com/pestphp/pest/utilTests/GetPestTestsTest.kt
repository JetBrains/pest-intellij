package com.pestphp.pest.utilTests

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.getPestTests
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class GetPestTestsTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/PestUtil")
    }

    @Test
    fun testMethodCallNamedTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedTest.php")

        assertTrue(runReadActionBlocking { file.getPestTests() }.isEmpty())
    }

    @Test
    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        assertTrue(runReadActionBlocking { file.getPestTests() }.isEmpty())
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        assertFalse(runReadActionBlocking { file.getPestTests() }.isEmpty())
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        assertFalse(runReadActionBlocking { file.getPestTests() }.isEmpty())
    }

    @Test
    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        assertFalse(runReadActionBlocking { file.getPestTests() }.isEmpty())
    }

    @Test
    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        assertTrue(runReadActionBlocking { file.getPestTests() }.isEmpty())
    }

    @Test
    fun testPestTestWithNamespaceIsPestTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithNamesapce.php")

        assertFalse(runReadActionBlocking { file.getPestTests() }.isEmpty())
    }

    @Test
    fun testNestedDescribeBlockTestsAreIncluded() {
        val file = myFixture.configureByFile("NestedDescribeFunctionCalls.php")

        val tests = runReadActionBlocking { file.getPestTests() }
        assertEquals(4, tests.size)
    }
}

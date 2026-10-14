package com.pestphp.pest.utilTests

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.isPestTestFile
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/PestUtil")
class IsPestTestFileTest {
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

        assertFalse(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        assertFalse(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        assertFalse(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testPestTestWithNamespaceIsPestTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithNamesapce.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testDescribeOnlyFileIsPestTest() {
        val file = myFixture.configureByFile("PestDescribeBlock.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testDescribeOnlyFileIsPestTestSmart() {
        val file = myFixture.configureByFile("PestDescribeBlock.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile(isSmart = true) })
    }

    @Test
    fun testNestedDescribeBlocksIsPestTest() {
        val file = myFixture.configureByFile("NestedDescribeFunctionCalls.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testNestedDescribeBlocksIsPestTestSmart() {
        val file = myFixture.configureByFile("NestedDescribeFunctionCalls.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile(isSmart = true) })
    }

    @Test
    fun testLifecycleHookFollowedByDescribeIsPestTest() {
        val file = myFixture.configureByFile("PestLifecycleAndDescribeBlock.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile() })
    }

    @Test
    fun testLifecycleHookFollowedByDescribeIsPestTestSmart() {
        val file = myFixture.configureByFile("PestLifecycleAndDescribeBlock.php")

        assertTrue(runReadActionBlocking { file.isPestTestFile(isSmart = true) })
    }
}

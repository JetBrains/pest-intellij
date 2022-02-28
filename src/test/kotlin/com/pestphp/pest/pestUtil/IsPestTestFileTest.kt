package com.pestphp.pest.pestUtil

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.isPestTestFile

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/PestUtil")
class IsPestTestFileTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testMethodCallNamedTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedTest.php")

        assertFalse(file.isPestTestFile())
    }

    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        assertFalse(file.isPestTestFile())
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        assertTrue(file.isPestTestFile())
    }

    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        assertTrue(file.isPestTestFile())
    }

    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        assertTrue(file.isPestTestFile())
    }

    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        assertFalse(file.isPestTestFile())
    }

    fun testPestTestWithNamespaceIsPestTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithNamesapce.php")

        assertTrue(file.isPestTestFile())
    }
}

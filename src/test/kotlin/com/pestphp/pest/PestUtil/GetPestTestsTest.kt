package com.pestphp.pest.PestUtil

import com.pestphp.pest.getPestTests
import com.pestphp.pest.tests.PestLightCodeFixture

class GetPestTestsTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testMethodCallNamedTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedTest.php")

        assertEmpty(file.getPestTests())
    }

    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        assertEmpty(file.getPestTests())
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        assertNotEmpty(file.getPestTests())
    }

    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        assertNotEmpty(file.getPestTests())
    }

    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        assertNotEmpty(file.getPestTests())
    }

    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        assertEmpty(file.getPestTests())
    }

    fun testPestTestWithNamespaceIsPestTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithNamesapce.php")

        assertNotEmpty(file.getPestTests())
    }
}

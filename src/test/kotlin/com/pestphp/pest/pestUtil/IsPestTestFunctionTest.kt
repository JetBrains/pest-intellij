package com.pestphp.pest.pestUtil

import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.isPestTestReference

class IsPestTestFunctionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testMethodCallNamedTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedTest.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertFalse(testElement.isPestTestReference())
    }

    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertFalse(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertTrue(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertTrue(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertTrue(testElement.isPestTestReference())
    }

    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertFalse(testElement.isPestTestReference())
    }
}

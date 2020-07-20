package com.pestphp.pest.PestUtil

import com.pestphp.pest.isPestTestFunction
import com.pestphp.pest.tests.PestLightCodeFixture

class IsPestTestFunctionTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
        return "src/test/resources/com/pestphp/pest/PestUtil/IsPestTestFunctionTest"
    }

    fun testMethodCallNamedTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedTest.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertFalse(testElement.isPestTestFunction())
    }

    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertFalse(testElement.isPestTestFunction())
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertTrue(testElement.isPestTestFunction())
    }

    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertTrue(testElement.isPestTestFunction())
    }

    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertTrue(testElement.isPestTestFunction())
    }
}
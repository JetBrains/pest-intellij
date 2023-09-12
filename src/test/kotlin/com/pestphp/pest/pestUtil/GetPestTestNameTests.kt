package com.pestphp.pest.pestUtil

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/PestUtil")
class GetPestTestNameTests : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertEquals("it basic", testElement.getPestTestName())
    }

    fun testFunctionCallNamedTestWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndClosure.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertEquals("basic", testElement.getPestTestName())
    }

    fun testFunctionCallNamedItWithConcatStringTest() {
        val file = myFixture.configureByFile("PestItFunctionCallWithConcatString.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertEquals("it basic supertest", testElement.getPestTestName())
    }

    fun testFunctionCallNamedTestWithConcatStringTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithConcatString.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertEquals("basic super", testElement.getPestTestName())
    }

    fun testFunctionCallNamedDescribeWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestDescribeBlock.php")

        val testElement = file.firstChild.lastChild.firstChild

        assertEquals("`sum` → ", testElement.getPestTestName())
    }
}

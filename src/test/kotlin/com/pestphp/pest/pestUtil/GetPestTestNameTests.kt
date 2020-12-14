package com.pestphp.pest.pestUtil

import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName

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
}

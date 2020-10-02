package com.pestphp.pest.PestUtil

import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import com.pestphp.pest.isPestTestReference
import com.pestphp.pest.tests.PestLightCodeFixture
import junit.framework.TestCase

class GetPestTestNameTests : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
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

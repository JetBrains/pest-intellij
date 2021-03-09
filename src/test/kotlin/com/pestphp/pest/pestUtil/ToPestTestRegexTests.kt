package com.pestphp.pest.pestUtil

import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.toPestTestRegex

class ToPestTestRegexTests : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testRegexContainsStartAndEndBounds() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php",
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.startsWith('^') == true)
        assertTrue(regex?.endsWith('$') == true)
    }

    fun testRegexContainsPestNamespacePrefix() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("P\\\\") == true)
    }

    fun testRegexContainsClassMethodSeparator() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("::") == true)
    }

    fun testRegexContainsItWhenItFunctionCall() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("it") == true)
    }

    fun testRegexEscapesParenthesis() {
        val file = myFixture.configureByFile(
            "PestTestFunctionCallWithParenthesis.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("\\(") == true)
        assertTrue(regex?.contains("\\)") == true)
    }

    fun testRegexEscapesCircumflexes() {
        val file = myFixture.configureByFile(
            "PestTestFunctionCallWithCircumflex.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("\\^") == true)
    }
}

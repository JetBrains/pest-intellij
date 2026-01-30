package com.pestphp.pest.utilTests

import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.toPestTestRegex

class ToPestTestRegexTests : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testRegexContainsStartBounds() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php",
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.startsWith('^') == true)
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

    fun testRegexEscapesPlusAndQuestionMark() {
        val file = myFixture.configureByFile(
            "PestTestWithPlusAndQuestionMark.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("\\+") == true)
        assertTrue(regex?.contains("\\?") == true)
    }

    fun testRegexEndOfLine() {
        val file = myFixture.configureByFile(
            "PestDescribeBlockAndTestFunctionEndOfLine.php"
        )

        val testElement = file.firstChild?.children?.map { it.firstChild }?.lastOrNull()
        val describeElement = file.firstChild?.children?.map { it.firstChild }?.firstOrNull()

        val testRegex = testElement?.toPestTestRegex("src")
        val describeRegex = describeElement?.toPestTestRegex("src")

        assertTrue(testRegex?.endsWith("$") == true)
        assertTrue(describeRegex?.endsWith("$") == false)
    }

    fun testRegexHandlesTestSuffix() {
        val file = myFixture.configureByFile(
            "Login.test.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertFalse(regex?.contains(".test") == true)
        assertTrue(regex?.contains("Login") == true)
    }

    fun testRegexHandlesSpecSuffix() {
        val file = myFixture.configureByFile(
            "User.spec.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertFalse(regex?.contains(".spec") == true)
        assertTrue(regex?.contains("User") == true)
    }

    fun testRegexHandlesEmojiInPath() {
        val regex = "it renders components".toPestTestRegex(
            "src",
            "/src/livewire⚡/Component.php",
            PhpPathMapper.create(this.project)
        )

        assertFalse(regex.contains("⚡"))
        assertTrue(regex.contains("livewire"))
        assertTrue(regex.contains("Component"))
    }

    fun testRegexHandlesDotInDirectoryName() {
        val file = myFixture.configureByFile(
            "dir.name/Test.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertFalse(regex?.contains("dir.name") == true)
        assertTrue(regex?.contains("dirname") == true)
    }

    fun testRegexHandlesMultipleDots() {
        val file = myFixture.configureByFile(
            "Login.integration.test.php"
        )

        val testElement = file.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertFalse(regex?.contains(".integration") == true)
        assertFalse(regex?.contains(".test") == true)
        assertTrue(regex?.contains("Login") == true)
    }
}

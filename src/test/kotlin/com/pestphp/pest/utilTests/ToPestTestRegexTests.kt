package com.pestphp.pest.utilTests

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.toPestTestRegex
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class ToPestTestRegexTests {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/PestUtil")
    }

    @Test
    fun testRegexContainsStartBounds() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php",
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertTrue(regex?.startsWith('^') == true)
    }

    @Test
    fun testRegexContainsPestNamespacePrefix() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertTrue(regex?.contains("P\\\\") == true)
    }

    @Test
    fun testRegexContainsClassMethodSeparator() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertTrue(regex?.contains("::") == true)
    }

    @Test
    fun testRegexContainsItWhenItFunctionCall() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertTrue(regex?.contains("it") == true)
    }

    @Test
    fun testRegexEscapesParenthesis() {
        val file = myFixture.configureByFile(
            "PestTestFunctionCallWithParenthesis.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertTrue(regex?.contains("\\(") == true)
        assertTrue(regex?.contains("\\)") == true)
    }

    @Test
    fun testRegexEscapesCircumflexes() {
        val file = myFixture.configureByFile(
            "PestTestFunctionCallWithCircumflex.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertTrue(regex?.contains("\\^") == true)
    }

    @Test
    fun testRegexEscapesPlusAndQuestionMark() {
        val file = myFixture.configureByFile(
            "PestTestWithPlusAndQuestionMark.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertTrue(regex?.contains("\\+") == true)
        assertTrue(regex?.contains("\\?") == true)
    }

    @Test
    fun testRegexEndOfLine() {
        val file = myFixture.configureByFile(
            "PestDescribeBlockAndTestFunctionEndOfLine.php"
        )

        val (testRegex, describeRegex) = runReadActionBlocking {
            val testElement = file.firstChild?.children?.map { it.firstChild }?.lastOrNull()
            val describeElement = file.firstChild?.children?.map { it.firstChild }?.firstOrNull()

            testElement?.toPestTestRegex("src") to describeElement?.toPestTestRegex("src")
        }

        assertTrue(testRegex?.endsWith("$") == true)
        assertTrue(describeRegex?.endsWith("$") == false)
    }

    @Test
    fun testRegexHandlesTestSuffix() {
        val file = myFixture.configureByFile(
            "Login.test.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertFalse(regex?.contains(".test") == true)
        assertTrue(regex?.contains("Login") == true)
    }

    @Test
    fun testRegexHandlesSpecSuffix() {
        val file = myFixture.configureByFile(
            "User.spec.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertFalse(regex?.contains(".spec") == true)
        assertTrue(regex?.contains("User") == true)
    }

    @Test
    fun testPrefixMatchControlsEndOfLineAnchor() {
        val mapper = PhpPathMapper.create(myFixture.project)

        val exact = "`my block`".toPestTestRegex("src", "/src/ExampleTest.php", mapper, prefixMatch = false)
        val prefix = "`my block`".toPestTestRegex("src", "/src/ExampleTest.php", mapper, prefixMatch = true)

        assertTrue(exact.endsWith("$"))
        assertFalse(prefix.endsWith("$"))
    }

    @Test
    fun testRegexHandlesEmojiInPath() {
        val regex = "it renders components".toPestTestRegex(
            "src",
            "/src/livewire⚡/Component.php",
            PhpPathMapper.create(myFixture.project),
            prefixMatch = false
        )

        assertFalse(regex.contains("⚡"))
        assertTrue(regex.contains("livewire"))
        assertTrue(regex.contains("Component"))
    }

    @Test
    fun testRegexHandlesDotInDirectoryName() {
        val file = myFixture.configureByFile(
            "dir.name/Test.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertFalse(regex?.contains("dir.name") == true)
        assertTrue(regex?.contains("dirname") == true)
    }

    @Test
    fun testRegexHandlesMultipleDots() {
        val file = myFixture.configureByFile(
            "Login.integration.test.php"
        )

        val regex = runReadActionBlocking { file.firstChild?.lastChild?.firstChild?.toPestTestRegex("src") }

        assertFalse(regex?.contains(".integration") == true)
        assertFalse(regex?.contains(".test") == true)
        assertTrue(regex?.contains("Login") == true)
    }
}

package com.pestphp.pest.pestUtil

import com.intellij.psi.PsiManager
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.toPestTestRegex

class ToPestTestRegexTests : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testRegexContainsStartAndEndBounds() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)

        val testElement = file?.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.startsWith('^') == true)
        assertTrue(regex?.endsWith('$') == true)
    }

    fun testRegexContainsPestNamespacePrefix() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)

        val testElement = file?.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("P\\\\") == true)
    }

    fun testRegexContainsClassMethodSeparator() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)

        val testElement = file?.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("::") == true)
    }

    fun testRegexContainsItWhenItFunctionCall() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)

        val testElement = file?.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("it") == true)
    }

    fun testRegexEscapesParenthesis() {
        val virtualFile = myFixture.copyFileToProject(
            "PestTestFunctionCallWithParenthesis.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)

        val testElement = file?.firstChild?.lastChild?.firstChild

        val regex = testElement?.toPestTestRegex("src")

        assertTrue(regex?.contains("\\(") == true)
        assertTrue(regex?.contains("\\)") == true)
    }
}

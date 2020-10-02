package com.pestphp.pest.PestUtil

import com.intellij.psi.PsiManager
import com.pestphp.pest.tests.PestLightCodeFixture
import com.pestphp.pest.toPestTestRegex

class ToPestTestRegexTests : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testRegexContainsStartAndEndBounds() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val testElement = file.firstChild.lastChild.firstChild

        val regex = testElement.toPestTestRegex("src")!!

        assertTrue(regex.startsWith('^'))
        assertTrue(regex.endsWith('$'))
    }

    fun testRegexContainsPestNamespacePrefix() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val testElement = file.firstChild.lastChild.firstChild

        val regex = testElement.toPestTestRegex("src")!!

        assertTrue(regex.contains("P\\\\"))
    }

    fun testRegexContainsClassMethodSeparator() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val testElement = file.firstChild.lastChild.firstChild

        val regex = testElement.toPestTestRegex("src")!!

        assertTrue(regex.contains("::"))
    }

    fun testRegexContainsItWhenItFunctionCall() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val testElement = file.firstChild.lastChild.firstChild

        val regex = testElement.toPestTestRegex("src")!!

        assertTrue(regex.contains("it"))
    }
}

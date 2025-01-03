package com.pestphp.pest.utilTests

import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isDescribeFunction

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

    fun testNestedDescribeFunctionCalls() {
        val file = myFixture.configureByFile("NestedDescribeFunctionCalls.php")

        val testElements = getAllPestTests(file.firstChild)

        listOf(
            "`SomeClass` → it works as well",
            "`SomeClass` → `SomeMethod` → it does not work",
            "`SomeClass` → `SomeMethod` → ",
            "`SomeClass` → ",
        ).zip(testElements).toMap().forEach { (expected, describeBlock) ->
            assertEquals(expected, describeBlock.getPestTestName())
        }
    }

    fun testArchFunctionCall() {
        val file = myFixture.configureByFile("PestArchFunctionCall.php")

        val testElements = file.firstChild.childrenOfType<Statement>().map { getPestTestFieldReference(it.firstChild) }

        listOf(
            "preset  → laravel ",
            "preset  → laravel  → ignoring 'A'",
            "preset  → laravel  → ignoring ['A']",
            "preset  → laravel  → ignoring ['A']",
            "expect 'src' → toUseStrictTypes  → not → toUse ['die', 'dd', 'dump']"
        ).zip(testElements).toMap().forEach { (expected, archTest) ->
            assertEquals(expected, archTest.getPestTestName())
        }
    }

    private fun getPestTestFieldReference(test: PsiElement): PsiElement {
        when (test.firstChild) {
            is FunctionReference, is FieldReference -> return getPestTestFieldReference(test.firstChild)
            else -> return test
        }
    }

    private fun getAllPestTests(root: PsiElement): List<PsiElement> {
        if (root is FunctionReferenceImpl && !root.isDescribeFunction()) return listOf(root)
        return root.children.fold(mutableListOf<PsiElement>()) { list, element ->
            list.addAll(
                getAllPestTests(element) +
                    if (root is FunctionReferenceImpl && root.isDescribeFunction()) listOf(root) else listOf()
            )
            list
        }
    }
}

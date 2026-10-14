package com.pestphp.pest.utilTests

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isDescribeFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/PestUtil")
class GetPestTestNameTests {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/PestUtil")
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        val testName = runReadActionBlocking {
            PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!.getPestTestName()
        }

        assertEquals("it basic", testName)
    }

    @Test
    fun testFunctionCallNamedTestWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndClosure.php")

        val testName = runReadActionBlocking {
            PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!.getPestTestName()
        }

        assertEquals("basic", testName)
    }

    @Test
    fun testFunctionCallNamedItWithConcatStringTest() {
        val file = myFixture.configureByFile("PestItFunctionCallWithConcatString.php")

        val testName = runReadActionBlocking {
            PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!.getPestTestName()
        }

        assertEquals("it basic supertest", testName)
    }

    @Test
    fun testFunctionCallNamedTestWithConcatStringTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithConcatString.php")

        val testName = runReadActionBlocking {
            PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!.getPestTestName()
        }

        assertEquals("basic super", testName)
    }

    @Test
    fun testFunctionCallNamedDescribeWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestDescribeBlock.php")

        val testName = runReadActionBlocking {
            PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!.getPestTestName()
        }

        assertEquals("`sum` → ", testName)
    }

    @Test
    fun testNestedDescribeFunctionCalls() {
        val file = myFixture.configureByFile("NestedDescribeFunctionCalls.php")

        val testNames = runReadActionBlocking {
            getAllPestTests(file.firstChild).map { it.getPestTestName() }
        }

        listOf(
            "`SomeClass` → it works as well",
            "`SomeClass` → `SomeMethod` → it does not work",
            "`SomeClass` → `SomeMethod` → ",
            "`SomeClass` → ",
        ).zip(testNames).toMap().forEach { (expected, actual) ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun testArchFunctionCall() {
        val file = myFixture.configureByFile("PestArchFunctionCall.php")

        val testNames = runReadActionBlocking {
            file.firstChild.childrenOfType<Statement>().map { getPestTestFieldReference(it.firstChild).getPestTestName() }
        }

        listOf(
            "preset  → laravel ",
            "preset  → laravel  → ignoring 'A'",
            "preset  → laravel  → ignoring ['A']",
            "preset  → laravel  → ignoring ['A']",
            "expect 'src' → toUseStrictTypes  → not → toUse ['die', 'dd', 'dump']"
        ).zip(testNames).toMap().forEach { (expected, actual) ->
            assertEquals(expected, actual)
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

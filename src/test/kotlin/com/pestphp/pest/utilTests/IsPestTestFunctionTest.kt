package com.pestphp.pest.utilTests

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.isPestTestReference

@TestDataPath("\$CONTENT_ROOT/../resources/com/pestphp/pest/PestUtil")
class IsPestTestFunctionTest : PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/PestUtil"

    fun testMethodCallNamedTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedTest.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertFalse(testElement.isPestTestReference())
    }

    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertFalse(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertTrue(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertTrue(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertTrue(testElement.isPestTestReference())
    }

    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertFalse(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedItWithConcatStringTest() {
        val file = myFixture.configureByFile("PestItFunctionCallWithConcatString.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertTrue(testElement.isPestTestReference())
    }

    fun testFunctionCallNamedTestWithConcatStringTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithConcatString.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertTrue(testElement.isPestTestReference())
    }
}

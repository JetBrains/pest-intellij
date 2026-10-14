package com.pestphp.pest.utilTests

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.isPestTestReference
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/PestUtil")
class IsPestTestFunctionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/PestUtil")
    }

    @Test
    fun testMethodCallNamedTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedTest.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertFalse(runReadActionBlocking { testElement.isPestTestReference() })
    }

    @Test
    fun testMethodCallNamedItIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedIt.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertFalse(runReadActionBlocking { testElement.isPestTestReference() })
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertTrue(runReadActionBlocking { testElement.isPestTestReference() })
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertTrue(runReadActionBlocking { testElement.isPestTestReference() })
    }

    @Test
    fun testFunctionCallNamedTestWithDescriptionAndHigherOrder() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithDescriptionAndHigherOrder.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertTrue(runReadActionBlocking { testElement.isPestTestReference() })
    }

    @Test
    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertFalse(runReadActionBlocking { testElement.isPestTestReference() })
    }

    @Test
    fun testFunctionCallNamedItWithConcatStringTest() {
        val file = myFixture.configureByFile("PestItFunctionCallWithConcatString.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertTrue(runReadActionBlocking { testElement.isPestTestReference() })
    }

    @Test
    fun testFunctionCallNamedTestWithConcatStringTest() {
        val file = myFixture.configureByFile("PestTestFunctionCallWithConcatString.php")

        val testElement = runReadActionBlocking { PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!! }

        assertTrue(runReadActionBlocking { testElement.isPestTestReference() })
    }
}

package com.pestphp.pest.goto

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PestTestFinderTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/goto/PestTestFinder")
    }

    @Test
    fun testPestTestIsTest() {
        val file = myFixture.configureByFile("test/App/UserTest.php")

        val isTest = runReadActionBlocking {
            val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!
            PestTestFinder().isTest(testElement)
        }

        assertTrue(isTest)
    }

    @Test
    fun testFileIsTest() {
        val file = myFixture.configureByFile("test/App/UserTest.php")

        assertTrue(runReadActionBlocking { PestTestFinder().isTest(file) })
    }

    @Test
    fun testRandomElementIsTest() {
        val file = myFixture.configureByFile("test/App/UserTest.php")

        assertTrue(runReadActionBlocking { PestTestFinder().isTest(file.firstChild.children.random()) })
    }

    @Test
    fun testClassIsNotTest() {
        val file = myFixture.configureByFile("test/App/MockTest.php")
        myFixture.testDataPath

        assertFalse(runReadActionBlocking { PestTestFinder().isTest(PsiTreeUtil.findChildOfType(file, PhpClass::class.java)!!) })
    }

    @Test
    fun testCanFindSourceElement() {
        val file = myFixture.configureByFile("App/User.php")

        val sourceElement = runReadActionBlocking {
            PestTestFinder().findSourceElement(
                PhpPsiUtil.findAllClasses(file as PhpFile).first().methods.first()
            )
        }
        assertSame(
            file,
            sourceElement
        )
    }

    @Test
    fun testFindTestFileForClass() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")

        val foundTest = runReadActionBlocking {
            PestTestFinder().findTestsForClass(PhpPsiUtil.findAllClasses(file as PhpFile).first()).first()
        }
        assertSame(
            testFile,
            foundTest,
        )
    }

    @Test
    fun testFindClassForTestFile() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")

        val (expectedClass, foundClass) = runReadActionBlocking {
            PhpPsiUtil.findAllClasses(file as PhpFile).first() to PestTestFinder().findClassesForTest(testFile.firstChild).first()
        }
        assertSame(
            expectedClass,
            foundClass
        )
    }

    @Test
    fun testFindTestsForMethod() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")
        val (foundTests, tests) = runReadActionBlocking {
            val method = PhpPsiUtil.findAllClasses(file as PhpFile).first().findMethodByName("isPestDeveloper")
            val tests = (testFile as PhpFile).getPestTests().filter {
                it.getPestTestName()?.contains("is pest developer") == true
            }
            PestTestFinder().findTestsForClass(method!!) to tests
        }

        assertEquals(tests.size, foundTests.size)
        assertEquals(tests.toSet(), foundTests.toSet())
    }

    @Test
    fun testFindMethodsForTest() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")
        val (foundClasses, methods) = runReadActionBlocking {
            val test = (testFile as PhpFile).getPestTests().first {
                it.getPestTestName() == "is pest developer"
            }
            val methods = PhpPsiUtil.findAllClasses(file as PhpFile).first().methods.filter { it.name.contains("is") }
            PestTestFinder().findClassesForTest(test) to methods
        }

        assertEquals(methods.size, foundClasses.size)
        assertEquals(methods.toSet(), foundClasses.toSet())
    }

    @Test
    fun testFindTestsForMethodInDescribeBlock() {
        val file = myFixture.configureByFile("App/User.php")
        myFixture.configureByFile("test/App/UserDescribeTest.php")
        val hasDescribeTest = runReadActionBlocking {
            val method = PhpPsiUtil.findAllClasses(file as PhpFile).first().findMethodByName("isPestDeveloper")

            val tests = PestTestFinder().findTestsForClass(method!!)

            tests.any { it.getPestTestName()?.contains("is pest developer in describe") == true }
        }

        assertTrue(hasDescribeTest)
    }

    @Test
    fun testFindClassForNonPestTestFileIsEmpty() {
        val file = myFixture.configureByFile("test/App/MockTest.php")

        assertTrue(runReadActionBlocking { PestTestFinder().findClassesForTest(file) }.isEmpty())
    }
}

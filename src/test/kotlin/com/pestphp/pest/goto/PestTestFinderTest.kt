package com.pestphp.pest.goto

import com.intellij.testFramework.UsefulTestCase
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import junit.framework.TestCase

class PestTestFinderTest : PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/goto/PestTestFinder"

    fun testPestTestIsTest() {
        val file = myFixture.configureByFile("test/App/UserTest.php")

        val testElement = PsiTreeUtil.findChildOfType(file, FunctionReference::class.java)!!

        assertTrue(PestTestFinder().isTest(testElement))
    }

    fun testFileIsTest() {
        val file = myFixture.configureByFile("test/App/UserTest.php")

        assertTrue(PestTestFinder().isTest(file))
    }

    fun testRandomElementIsTest() {
        val file = myFixture.configureByFile("test/App/UserTest.php")

        assertTrue(PestTestFinder().isTest(file.firstChild.children.random()))
    }

    fun testClassIsNotTest() {
        val file = myFixture.configureByFile("test/App/MockTest.php")
        myFixture.testDataPath

        assertFalse(PestTestFinder().isTest(PsiTreeUtil.findChildOfType(file, PhpClass::class.java)!!))
    }

    fun testCanFindSourceElement() {
        val file = myFixture.configureByFile("App/User.php")

        TestCase.assertSame(
            file,
            PestTestFinder().findSourceElement(
                PhpPsiUtil.findAllClasses(file as PhpFile).first().methods.first()
            )
        )
    }

    fun testFindTestFileForClass() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")

        TestCase.assertSame(
            testFile,
            PestTestFinder().findTestsForClass(PhpPsiUtil.findAllClasses(file as PhpFile).first()).first(),
        )
    }

    fun testFindClassForTestFile() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")

        TestCase.assertSame(
            PhpPsiUtil.findAllClasses(file as PhpFile).first(),
            PestTestFinder().findClassesForTest(testFile.firstChild).first()
        )
    }

    fun testFindTestsForMethod() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")
        val method = PhpPsiUtil.findAllClasses(file as PhpFile).first().findMethodByName("isPestDeveloper")
        val tests = (testFile as PhpFile).getPestTests().filter {
            it.getPestTestName()?.contains("is pest developer") == true
        }

        UsefulTestCase.assertSameElements(
            PestTestFinder().findTestsForClass(method!!),
            tests
        )
    }

    fun testFindMethodsForTest() {
        val file = myFixture.configureByFile("App/User.php")
        val testFile = myFixture.configureByFile("test/App/UserTest.php")
        val test = (testFile as PhpFile).getPestTests().first {
            it.getPestTestName() == "is pest developer"
        }
        val methods = PhpPsiUtil.findAllClasses(file as PhpFile).first().methods.filter { it.name.contains("is") }

        UsefulTestCase.assertSameElements(
            PestTestFinder().findClassesForTest(test),
            methods
        )
    }

    fun testFindTestsForMethodInDescribeBlock() {
        val file = myFixture.configureByFile("App/User.php")
        myFixture.configureByFile("test/App/UserDescribeTest.php")
        val method = PhpPsiUtil.findAllClasses(file as PhpFile).first().findMethodByName("isPestDeveloper")

        val tests = PestTestFinder().findTestsForClass(method!!)

        assertTrue(tests.any { it.getPestTestName()?.contains("is pest developer in describe") == true })
    }

    fun testFindClassForNonPestTestFileIsEmpty() {
        val file = myFixture.configureByFile("test/App/MockTest.php")

        assertEmpty(PestTestFinder().findClassesForTest(file))
    }
}

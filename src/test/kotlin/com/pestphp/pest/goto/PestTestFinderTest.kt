package com.pestphp.pest.goto

import com.intellij.testFramework.UsefulTestCase
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName
import junit.framework.TestCase

class PestTestFinderTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/goto/PestTestFinder"
    }

    fun testPestTestIsTest() {
        val file = myFixture.configureByFile("test/App/UserTest.php")

        val testElement = file.firstChild.lastChild.firstChild

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
        val tests = testFile.firstChild.children.map { it.firstChild }.filter {
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
        val test = testFile.firstChild.children.map { it.firstChild }.first {
            it.getPestTestName() == "is pest developer"
        }
        val methods = PhpPsiUtil.findAllClasses(file as PhpFile).first().methods.filter { it.name.contains("is") }

        UsefulTestCase.assertSameElements(
            PestTestFinder().findClassesForTest(test),
            methods
        )
    }
}

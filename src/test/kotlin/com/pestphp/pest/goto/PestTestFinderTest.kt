package com.pestphp.pest.goto

import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.pestphp.pest.PestLightCodeFixture
import junit.framework.TestCase

class PestTestFinderTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
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
}

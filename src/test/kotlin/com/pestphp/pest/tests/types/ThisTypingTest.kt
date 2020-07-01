package com.pestphp.pest.tests.types

import com.pestphp.pest.tests.PestLightCodeFixture

class ThisTypingTest: PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("TestCase.php")
        myFixture.copyFileToProject("this/itTest.txt", "itTest.php")
        myFixture.copyFileToProject("this/testTest.txt", "testTest.php")
    }

    override fun getTestDataPath(): String? {
        return basePath + "types/fixtures"
    }

    fun testItFunction() {
        myFixture.configureByFile("itTest.php")

        assertCompletion("expectException", "expectExceptionCode")
    }

    fun testTestFunction() {
        myFixture.configureByFile("testTest.php")

        assertCompletion("expectException", "expectExceptionCode")
    }
}
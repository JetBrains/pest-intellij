package com.pestphp.pest.tests.types

class FunctionTypeTest: BaseTypeTest() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("function", "/")
    }

    fun testTestFunction() {
        myFixture.configureByFile("testTest.php")
        assertFunctionCompletion()
    }

    private fun assertFunctionCompletion() {
        assertCompletion("expectException", "expectExceptionCode")
    }
}
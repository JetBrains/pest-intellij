package com.pestphp.pest.types

class FunctionTypeTest : BaseTypeTestCase() {
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

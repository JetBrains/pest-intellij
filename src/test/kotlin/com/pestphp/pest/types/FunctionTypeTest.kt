package com.pestphp.pest.types

import org.junit.Ignore

@Ignore("AT-3959")
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

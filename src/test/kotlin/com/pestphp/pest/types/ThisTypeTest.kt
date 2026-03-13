package com.pestphp.pest.types

import org.junit.Ignore

@Ignore("AT-3959")
class ThisTypeTest : BaseTypeTestCase() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("this", "/")
    }

    fun testItFunction() {
        myFixture.configureByFile("itTest.php")
        assertThisCompletion()
    }

    fun testTestFunction() {
        myFixture.configureByFile("testTest.php")
        assertThisCompletion()
    }

    fun testShortLambda() {
        myFixture.configureByFile("itShortLambdaTest.php")
        assertThisCompletion()
    }

    fun testBeforeEach() {
        myFixture.configureByFile("beforeEach.php")
        assertThisCompletion()
    }

    private fun assertThisCompletion() {
        assertCompletion(
            "expectException",
            "expectExceptionCode",
            "someProtectedMethod",
            "someStaticMethod",
            "protectedField",
        )
    }
}

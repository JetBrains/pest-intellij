package com.pestphp.pest.tests.types

class ThisTypeTest: BaseTypeTest() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("this", "/")
    }

    fun testItFunction() {
        myFixture.configureByFile("itTest.php")

        assertCompletion("expectException", "expectExceptionCode")
    }

    fun testTestFunction() {
        myFixture.configureByFile("testTest.php")

        assertCompletion("expectException", "expectExceptionCode")
    }

    fun testShortLambda() {
        myFixture.configureByFile("itShortLambdaTest.php")

        assertCompletion("expectException", "expectExceptionCode")
    }
}
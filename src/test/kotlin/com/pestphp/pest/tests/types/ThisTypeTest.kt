package com.pestphp.pest.tests.types

class ThisTypeTest: BaseTypeTest() {
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
        assertCompletion("expectException", "expectExceptionCode")
    }
}
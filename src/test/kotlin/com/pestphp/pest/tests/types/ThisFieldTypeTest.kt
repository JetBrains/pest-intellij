package com.pestphp.pest.tests.types

class ThisFieldTypeTest : BaseTypeTest() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("thisField", "tests")
    }

    fun testBeforeEach() {
        myFixture.configureByFile("tests/beforeEach.php")

        assertCompletion("a", "b")
    }

    fun testAfterEach() {
        myFixture.configureByFile("tests/afterEach.php")

        assertCompletion("a", "b")
    }
}

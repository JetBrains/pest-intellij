package com.pestphp.pest.tests.types

class ThisFieldCompletionTest : BaseTypeTest() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("thisField", "tests")
    }

    fun testFieldCompletions() {
        myFixture.configureByFile("tests/beforeEachCompletion.php")

        assertCompletion("foo")
    }
}

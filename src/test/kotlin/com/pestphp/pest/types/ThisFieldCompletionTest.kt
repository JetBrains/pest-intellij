package com.pestphp.pest.types

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

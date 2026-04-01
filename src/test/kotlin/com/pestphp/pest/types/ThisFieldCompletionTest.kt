package com.pestphp.pest.types

class ThisFieldCompletionTest : BaseTypeTestCase() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("thisField", "tests")
    }

    fun testFieldCompletions() {
        myFixture.configureByFile("tests/beforeEachCompletion.php")

        assertCompletion("foo")
    }

    fun testFieldCompletionWithNamespace() {
        myFixture.configureByFile("tests/beforeEachNamespaceCompletion.php")

        assertCompletion("foo")
    }
}

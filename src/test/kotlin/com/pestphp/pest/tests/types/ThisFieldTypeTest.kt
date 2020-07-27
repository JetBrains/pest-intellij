package com.pestphp.pest.tests.types

class ThisFieldTypeTest: BaseTypeTest() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("thisField", "/")
    }

    fun testBeforeEach() {
        myFixture.configureByFile("beforeEach.php")

        assertCompletion("a", "b")
    }

    fun testBeforeAll() {
        myFixture.configureByFile("beforeAll.php")

        assertCompletion("a", "b")
    }

    fun testAfterEach() {
        myFixture.configureByFile("afterEach.php")

        assertCompletion("a", "b")
    }
}
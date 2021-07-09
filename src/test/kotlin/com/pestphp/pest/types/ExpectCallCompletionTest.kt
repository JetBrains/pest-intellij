package com.pestphp.pest.types

class ExpectCallCompletionTest : BaseTypeTest() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("expect", "tests")
    }

    fun testFieldCompletions() {
        myFixture.configureByFile("tests/expectCallCompletion.php")

        assertCompletion("someExtend")
    }

    fun testFieldCompletionsChainedAndProperty() {
        myFixture.configureByFile("tests/expectCallCompletionChainedAndProperty.php")

        assertCompletion("someExtend")
    }

    fun testFieldCompletionsChainedAndMethod() {
        myFixture.configureByFile("tests/expectCallCompletionChainedAndMethod.php")

        assertCompletion("someExtend")
    }
}

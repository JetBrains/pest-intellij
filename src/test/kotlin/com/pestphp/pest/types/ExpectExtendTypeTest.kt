package com.pestphp.pest.types

class ExpectExtendTypeTest: BaseTypeTest() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("stubs.php")
        myFixture.copyDirectoryToProject("expect", "tests")
    }

    fun testExpectExtend() {
        myFixture.configureByFile("tests/expectExtendReturnType.php")

        assertCompletion("toBe", "toBeEmpty")
    }

    fun testExpectInvalidExtendNoReturnType() {
        myFixture.configureByFile("tests/expectInvalidExtendNoReturnType.php")

        assertNoCompletion()
    }

    fun testExtendCallOnNonExpectFunction() {
        myFixture.configureByFile("tests/expectExtendCallOnNonExpectFunction.php")

        assertNoCompletion()
    }

    fun testExtendCallOnChainedExpectation() {
        myFixture.configureByFile("tests/extendCallOnChainedExpectation.php")

        assertCompletion("toBe", "toBeEmpty")
    }
}
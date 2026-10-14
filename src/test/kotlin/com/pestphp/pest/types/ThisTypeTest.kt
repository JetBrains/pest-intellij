package com.pestphp.pest.types

import com.intellij.testFramework.junit5.TestApplication
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class ThisTypeTest : BaseTypeTestCase() {
    @BeforeEach
    fun setUpTest() {
        myFixture.copyDirectoryToProject("this", "/")
    }

    @Test
    fun testItFunction() {
        myFixture.configureByFile("itTest.php")
        assertThisCompletion()
    }

    @Test
    fun testTestFunction() {
        myFixture.configureByFile("testTest.php")
        assertThisCompletion()
    }

    @Test
    fun testShortLambda() {
        myFixture.configureByFile("itShortLambdaTest.php")
        assertThisCompletion()
    }

    @Test
    fun testBeforeEach() {
        myFixture.configureByFile("beforeEach.php")
        assertThisCompletion()
    }

    private fun assertThisCompletion() {
        PestTestUtil.assertCompletion(
            myFixture,
            "expectException",
            "expectExceptionCode",
            "someProtectedMethod",
            "someStaticMethod",
            "protectedField",
        )
    }
}

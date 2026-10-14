package com.pestphp.pest.types

import com.intellij.testFramework.junit5.TestApplication
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class FunctionTypeTest : BaseTypeTestCase() {
    @BeforeEach
    fun setUpTest() {
        myFixture.copyDirectoryToProject("function", "/")
    }

    @Test
    fun testTestFunction() {
        myFixture.configureByFile("testTest.php")
        assertFunctionCompletion()
    }

    private fun assertFunctionCompletion() {
        PestTestUtil.assertCompletion(myFixture, "expectException", "expectExceptionCode")
    }
}

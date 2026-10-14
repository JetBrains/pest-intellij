package com.pestphp.pest.types

import com.intellij.testFramework.junit5.TestApplication
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class ThisFieldCompletionTest : BaseTypeTestCase() {
    @BeforeEach
    fun setUpTest() {
        myFixture.copyDirectoryToProject("thisField", "tests")
    }

    @Test
    fun testFieldCompletions() {
        myFixture.configureByFile("tests/beforeEachCompletion.php")

        PestTestUtil.assertCompletion(myFixture, "foo")
    }

    @Test
    fun testFieldCompletionWithNamespace() {
        myFixture.configureByFile("tests/beforeEachNamespaceCompletion.php")

        PestTestUtil.assertCompletion(myFixture, "foo")
    }
}

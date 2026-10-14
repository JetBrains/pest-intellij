package com.pestphp.pest.types

import com.intellij.testFramework.junit5.TestApplication
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class ThisFieldTypeTest : BaseTypeTestCase() {
    @BeforeEach
    fun setUpTest() {
        myFixture.copyDirectoryToProject("thisField", "tests")
    }

    @Test
    fun testBeforeEach() {
        myFixture.configureByFile("tests/beforeEach.php")

        PestTestUtil.assertCompletion(myFixture, "a", "b")
    }

    @Test
    fun testBeforeEachNamespace() {
        myFixture.configureByFile("tests/beforeEachNamespace.php")

        PestTestUtil.assertCompletion(myFixture, "a", "b")
    }

    @Test
    fun testAfterEach() {
        myFixture.configureByFile("tests/afterEach.php")

        PestTestUtil.assertCompletion(myFixture, "a", "b")
    }

    @Test
    fun testAfterEachNamespace() {
        myFixture.configureByFile("tests/afterEachNamespace.php")

        PestTestUtil.assertCompletion(myFixture, "a", "b")
    }
}

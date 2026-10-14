package com.pestphp.pest.configuration

import com.intellij.execution.ExecutionException
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PestVersionParserTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
    }

    private fun doTest(output: String, expected: String) {
        val version = PestVersionDetector.instance.parse(output)
        assertNotNull(version)
        assertEquals(expected, version)
    }

    private fun doFailedTest(output: String) {
        val expectedMessage = PestBundle.message("PEST_CONFIGURATION_UI_CAN_NOT_PARSE_VERSION", output)
        val exception = assertThrows(ExecutionException::class.java) {
            PestVersionDetector.instance.parse(output)
        }
        assertTrue(exception.message!!.contains(expectedMessage), "message was expected to contain '$expectedMessage', but got: '${exception.message}'")
    }

    @Test
    fun testPestOutputBeforeV2() {
        doTest("""
            Pest    1.21.0
            PHPUnit 9.6.15
        """.trimIndent(), "1.21.0")
    }

    @Test
    fun testPestOutputAfterV2() {
        doTest("""
            
              Pest Testing Framework 2.21.0.  

        """.trimIndent(), "2.21.0")
    }

    @Test
    fun testIncorrectFormatBeforeV2() {
        doFailedTest("Some text 1.21.0")
    }

    @Test
    fun testIncorrectFormatAfterV2() {
        doFailedTest("""
            
              Pest 2.21.0.  

        """.trimIndent())
    }

    @Test
    fun testIncorrectVersionFormat() {
        doFailedTest("Pest 1.21")
    }
}
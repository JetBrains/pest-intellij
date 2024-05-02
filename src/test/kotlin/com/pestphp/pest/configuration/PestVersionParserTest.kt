package com.pestphp.pest.configuration

import com.intellij.execution.ExecutionException
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestLightCodeFixture

class PestVersionParserTest : PestLightCodeFixture() {
    private fun doTest(output: String, expected: String) {
        val version = PestVersionDetector.instance.parse(output)
        assertNotNull(version)
        assertEquals(version, expected)
    }

    private fun doFailedTest(output: String) {
        assertThrows(ExecutionException::class.java, PestBundle.message("PEST_CONFIGURATION_UI_CAN_NOT_PARSE_VERSION", output)) {
            PestVersionDetector.instance.parse(output)
        }
    }

    fun testPestOutputBeforeV2() {
        doTest("""
            Pest    1.21.0
            PHPUnit 9.6.15
        """.trimIndent(), "1.21.0")
    }

    fun testPestOutputAfterV2() {
        doTest("""
            
              Pest Testing Framework 2.21.0.  

        """.trimIndent(), "2.21.0")
    }

    fun testIncorrectFormatBeforeV2() {
        doFailedTest("Some text 1.21.0")
    }

    fun testIncorrectFormatAfterV2() {
        doFailedTest("""
            
              Pest 2.21.0.  

        """.trimIndent())
    }

    fun testIncorrectVersionFormat() {
        doFailedTest("Pest 1.21")
    }
}
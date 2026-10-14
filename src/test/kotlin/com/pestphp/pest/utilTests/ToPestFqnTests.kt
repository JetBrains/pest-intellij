package com.pestphp.pest.utilTests

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.toPestFqn
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class ToPestFqnTests {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/PestUtil")
    }

    @Test
    fun testCanGeneratePqn() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php",
        )

        val pqn = runReadActionBlocking { file?.firstChild?.lastChild?.firstChild?.toPestFqn() }

        assertTrue(pqn?.any { it.contains("::") } == true)
        assertTrue(pqn?.any { it.contains("pest_qn:") } == true)
    }
}

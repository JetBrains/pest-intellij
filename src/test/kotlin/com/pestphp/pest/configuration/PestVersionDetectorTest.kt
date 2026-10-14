package com.pestphp.pest.configuration

import com.intellij.execution.ExecutionException
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestTestUtil
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PestVersionDetectorTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
    }

    @Test
    fun testThrowsOnRemoteInterpreter() {
        val versionDetector = PestVersionDetector()
        val mockInterpreter = mockk<PhpInterpreter>()
        every { mockInterpreter.isRemote } returns true

        val expectedMessage = PestBundle.message("PEST_VERSION_IS_NOT_SUPPORTED_FOR_REMOTE_INTERPRETER")
        val exception = assertThrows(ExecutionException::class.java) {
            versionDetector.getVersion(myFixture.project, mockInterpreter, null)
        }
        assertTrue(exception.message!!.contains(expectedMessage), "message was expected to contain '$expectedMessage', but got: '${exception.message}'")
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }
}

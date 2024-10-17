package com.pestphp.pest.configuration

import com.intellij.execution.ExecutionException
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestLightCodeFixture
import io.mockk.every
import io.mockk.mockk

class PestVersionDetectorTest : PestLightCodeFixture() {
    fun testThrowsOnRemoteInterpreter() {
        val versionDetector = PestVersionDetector()
        val mockInterpreter = mockk<PhpInterpreter>()
        every { mockInterpreter.isRemote } returns true

        assertThrows(
            ExecutionException::class.java,
            PestBundle.message("PEST_VERSION_IS_NOT_SUPPORTED_FOR_REMOTE_INTERPRETER")
        ) {
            versionDetector.getVersion(project, mockInterpreter, null)
        }
    }
}
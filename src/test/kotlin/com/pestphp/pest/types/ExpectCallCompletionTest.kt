package com.pestphp.pest.types

import com.intellij.testFramework.replaceService
import com.jetbrains.php.composer.lib.ComposerLibraryManager
import org.junit.Ignore
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.TimeUnit

@Ignore
class ExpectCallCompletionTest : BaseTypeTestCase() {
    override fun setUp() {
        super.setUp()

        val dir = myFixture.copyDirectoryToProject("expect", "tests")
        myFixture.copyFileToProject("stubs.php")

        val composerMock = mock<ComposerLibraryManager> {
            on { findVendorDirForUpsource() } doReturn dir
        }
        project.replaceService(
            ComposerLibraryManager::class.java,
            composerMock,
            testRootDisposable
        )
    }

    fun testFieldCompletions() {
        myFixture.configureByFile("tests/expectCallCompletion.php")

        waitForAppLeakingThreads(10, TimeUnit.SECONDS)

        assertCompletion("someExtend")
    }

    fun testFieldCompletionsChainedNotProperty() {
        myFixture.configureByFile("tests/expectCallCompletionChainedNotProperty.php")

        assertCompletion("someExtend")
    }

    fun testFieldCompletionsChainedNotMethod() {
        myFixture.configureByFile("tests/expectCallCompletionChainedNotMethod.php")

        assertCompletion("someExtend")
    }
}
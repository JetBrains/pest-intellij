package com.pestphp.pest.types

import com.intellij.openapi.components.service
import com.intellij.testFramework.replaceService
import com.jetbrains.php.composer.lib.ComposerLibraryManager
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.services.ExpectationFileService
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.TimeUnit

class ExpectCallCompletionTest : BaseTypeTest() {
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
        val file = myFixture.configureByFile("tests/expectCallCompletion.php")

        val service = project.service<ExpectationFileService>()
        service.updateExtends(file as PhpFile, listOf())
        service.generateFile {}
        waitForAppLeakingThreads(10, TimeUnit.SECONDS)

        assertCompletion("someExtend")
    }

    fun testFieldCompletionsChainedNotProperty() {
        val file = myFixture.configureByFile("tests/expectCallCompletionChainedNotProperty.php")

        val service = project.service<ExpectationFileService>()
        service.updateExtends(file as PhpFile, listOf())
        service.generateFile {}
        waitForAppLeakingThreads(10, TimeUnit.SECONDS)

        assertCompletion("someExtend")
    }

    fun testFieldCompletionsChainedNotMethod() {
        val file = myFixture.configureByFile("tests/expectCallCompletionChainedNotMethod.php")

        val service = project.service<ExpectationFileService>()
        service.updateExtends(file as PhpFile, listOf())
        service.generateFile {}
        waitForAppLeakingThreads(10, TimeUnit.SECONDS)

        assertCompletion("someExtend")
    }
}

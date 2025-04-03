package com.pestphp.pest.types

import com.intellij.psi.PsiElement
import com.intellij.testFramework.replaceService
import com.jetbrains.php.composer.configData.ComposerConfigManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Ignore
import java.util.concurrent.TimeUnit

@Ignore
class ExpectCallCompletionTest : BaseTypeTestCase() {
    override fun setUp() {
        super.setUp()

        val dir = myFixture.copyDirectoryToProject("expect", "tests")
        myFixture.addFileToProject("composer.json", "")

        val composerConfigManagerMock = mockk<ComposerConfigManager>(relaxUnitFun = true) {
            every { getConfig(null as PsiElement?) } returns dir
        }
        project.replaceService(
            ComposerConfigManager::class.java,
            composerConfigManagerMock,
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
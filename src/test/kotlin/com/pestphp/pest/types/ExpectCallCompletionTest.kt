package com.pestphp.pest.types

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.common.waitForAppLeakingThreads
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.TestDisposable
import com.intellij.testFramework.replaceService
import com.intellij.testFramework.runInEdtAndWait
import com.jetbrains.php.composer.configData.ComposerConfigManager
import com.pestphp.pest.PestTestUtil
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

@TestApplication
class ExpectCallCompletionTest : BaseTypeTestCase() {
    @TestDisposable
    lateinit var disposable: Disposable

    @BeforeEach
    fun setUpTest() {
        val dir = myFixture.copyDirectoryToProject("expect", "tests")
        myFixture.addFileToProject("composer.json", "")

        val composerConfigManagerMock = mockk<ComposerConfigManager>(relaxed = true) {
            every { getConfig(null as PsiElement?) } returns dir
        }
        myFixture.project.replaceService(
            ComposerConfigManager::class.java,
            composerConfigManagerMock,
            disposable
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    @Test
    fun testFieldCompletions() {
        myFixture.configureByFile("tests/expectCallCompletion.php")

        runInEdtAndWait {
            PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
            waitForAppLeakingThreads(ApplicationManager.getApplication(), 10, TimeUnit.SECONDS)
        }

        PestTestUtil.assertCompletion(myFixture, "someExtend")
    }

    @Test
    fun testFieldCompletionsChainedNotProperty() {
        myFixture.configureByFile("tests/expectCallCompletionChainedNotProperty.php")

        PestTestUtil.assertCompletion(myFixture, "someExtend")
    }

    @Test
    fun testFieldCompletionsChainedNotMethod() {
        myFixture.configureByFile("tests/expectCallCompletionChainedNotMethod.php")

        PestTestUtil.assertCompletion(myFixture, "someExtend")
    }
}

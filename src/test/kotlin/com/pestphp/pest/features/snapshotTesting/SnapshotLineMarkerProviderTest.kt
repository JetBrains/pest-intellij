package com.pestphp.pest.features.snapshotTesting

import com.intellij.codeInsight.daemon.GutterMark
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class SnapshotLineMarkerProviderTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/snapshotTesting")
    }

    @Test
    fun testCanProvideIconForSnapshotAssertion() {
        val file = myFixture.configureByFile("allSnapshotAssertions.php")

        val snapshotCalls = runReadActionBlocking {
            PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
                .filter { it.isSnapshotAssertionCall }
        }
        assertFalse(snapshotCalls.isEmpty())

        assertEquals(snapshotCalls.count(), findAllGutters().size)
    }

    @Test
    fun testDoNotShowIconForSnapshotUse() {
        myFixture.configureByFile("snapshotAssertionUseStatement.php")

        assertEquals(0, findAllGutters().size)
    }

    private fun findAllGutters(): List<GutterMark> = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            myFixture.findAllGutters()
        }
    }
}

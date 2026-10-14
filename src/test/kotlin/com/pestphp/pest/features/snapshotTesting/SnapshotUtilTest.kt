package com.pestphp.pest.features.snapshotTesting

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class SnapshotUtilTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/snapshotTesting")
    }

    @Test
    fun testIsSnapshotAssertion() {
        val file = myFixture.configureByFile("allSnapshotAssertions.php")

        val isSnapshotAssertionCalls = runReadActionBlocking {
            PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java).map { it.isSnapshotAssertionCall }
        }
        assertFalse(isSnapshotAssertionCalls.isEmpty())

        isSnapshotAssertionCalls.forEach { assertTrue(it) }
    }

    @Test
    fun testIsNotSnapshotAssertion() {
        val file = myFixture.configureByFile("nonSnapshotAssertions.php")

        val isSnapshotAssertionCall = runReadActionBlocking {
            PsiTreeUtil.findChildOfType(file, FunctionReferenceImpl::class.java)!!.isSnapshotAssertionCall
        }

        assertFalse(isSnapshotAssertionCall)
    }

    @Test
    fun testCanFindSnapshotFiles() {
        myFixture.copyFileToProject(
            "tests/__snapshots__/snapshotTest__it_renders_correctly__1.txt",
            "tests/__snapshots__/snapshotTest__it_renders_correctly__1.txt"
        )

        val file = myFixture.configureByFile("snapshotTest.php")

        val snapshotFiles = runReadActionBlocking {
            PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
                .first { it.isSnapshotAssertionCall }
                .snapshotFiles
        }

        assertEquals(1, snapshotFiles.size)
    }
}
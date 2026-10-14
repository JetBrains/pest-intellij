package com.pestphp.pest.annotator

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.application.EDT
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PestAnnotatorTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/annotator")
        myFixture.copyDirectoryToProject("stub", ".")
    }

    @Test
    fun testHasDuplicateCustomExpectation() {
        myFixture.configureByFile("DuplicateCustomExpectation.php")

        checkHighlighting()
    }

    @Test
    fun testDeleteDuplicateCustomExpectation() {
        myFixture.configureByFile("DuplicateCustomExpectation.php")

        checkHighlighting { quickFixes ->
            quickFixes.first { it.familyName == PestBundle.message("QUICK_FIX_DELETE_CUSTOM_EXPECTATION", "toBeOne") }
        }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateCustomExpectation.afterDelete.php")
    }

    @Test
    fun testNavigateToNextDuplicateCustomExpectation() {
        myFixture.configureByFile("DuplicateCustomExpectation.php")

        checkHighlighting { quickFixes ->
            quickFixes.last { it.familyName == PestBundle.message("INTENTION_NAVIGATE_TO_DUPLICATE_CUSTOM_EXPECTATION") }
        }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateCustomExpectation.afterNavigate.php")
    }

    @Test
    fun testNoDuplicateCustomExpectation() {
        myFixture.configureByFile("NoDuplicateCustomExpectation.php")

        checkHighlighting()
    }

    @Test
    fun testHasDuplicateTest() {
        myFixture.configureByFile("DuplicateTestName.php")

        checkHighlighting()
    }

    @Test
    fun testDeleteDuplicateTest() {
        myFixture.configureByFile("DuplicateTestName.php")

        checkHighlighting { quickFixes ->
            quickFixes.first { it.familyName == PestBundle.message("QUICK_FIX_DELETE_TEST", "basic") }
        }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateTestName.afterDelete.php")
    }

    @Test
    fun testNavigateToDuplicateTest() {
        myFixture.configureByFile("DuplicateTestName.php")

        checkHighlighting { quickFixes ->
            quickFixes.last { it.familyName == PestBundle.message("INTENTION_NAVIGATE_TO_DUPLICATE_TEST_NAME") }
        }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateTestName.afterNavigate.php")
    }

    @Test
    fun testNoDuplicateTest() {
        myFixture.configureByFile("NoDuplicateTestName.php")

        checkHighlighting()
    }

    @Test
    fun testHasDuplicateTestInDescribeBlock() {
        myFixture.configureByFile("DuplicateTestNameInDescribeBlock.php")

        checkHighlighting()
    }

    private fun checkHighlighting(): Unit = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            myFixture.checkHighlighting()
        }
    }

    private fun <T> checkHighlighting(pickQuickFix: (List<IntentionAction>) -> T): T = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            myFixture.checkHighlighting()
            pickQuickFix(myFixture.getAllQuickFixes())
        }
    }
}

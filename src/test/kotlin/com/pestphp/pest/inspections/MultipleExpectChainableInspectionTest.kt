package com.pestphp.pest.inspections

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.application.EDT
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/inspections")
class MultipleExpectChainableInspectionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/inspections")
        myFixture.enableInspections(MultipleExpectChainableInspection::class.java)
    }

    @Test
    fun testHasMultipleExpectCall() {
        myFixture.configureByFile("MultipleExpectCall.php")

        checkHighlighting()
    }

    @Test
    fun testSingleExpectCall() {
        myFixture.configureByFile("SingleExpectCall.php")

        checkHighlighting()
    }

    @Test
    fun testReplacesMultipleExpectCallToChain() {
        myFixture.configureByFile("MultipleExpectCall.php")

        checkHighlighting { it.first() }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCall.after.php")
    }

    @Test
    fun testHasExpectCallsWithOtherStatementsBetween() {
        myFixture.configureByFile("ExpectCallsWithOtherStatementsBetween.php")

        checkHighlighting()
    }

    @Test
    fun testHasMultipleExpectCallsWithOtherStatementsBetween() {
        myFixture.configureByFile("MultipleExpectCallsWithOtherStatementsBetween.php")

        checkHighlighting()
    }

    @Test
    fun testReplaceMultipleExpectCalls() {
        myFixture.configureByFile("MultipleExpectCallsWithOtherStatementsBetween.php")

        checkHighlighting { it.first() }.run { myFixture.launchAction(this) }
        getAllQuickFixes().last().run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCallsWithOtherStatementsBetween.after.php")
    }

    @Test
    fun testReplaceCanBeCalledOnFirstStatement() {
        myFixture.configureByFile("MultipleExpectCall.php")

        checkHighlighting { it.first() }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCall.after.php")
    }

    @Test
    fun testReplaceCanBeCalledOnLastStatement() {
        myFixture.configureByFile("MultipleExpectCall.php")

        checkHighlighting { it.last() }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCall.after.php")
    }

    @Test
    fun testReplaceCanBeCalledOnSecondaryStatement() {
        myFixture.configureByFile("ManyExpectCall.php")

        checkHighlighting { it[3] }.run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("ManyExpectCall.after.php")
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

    private fun getAllQuickFixes(): List<IntentionAction> = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            myFixture.getAllQuickFixes()
        }
    }
}

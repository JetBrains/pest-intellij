package com.pestphp.pest.inspections

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/inspections")
class MultipleExpectChainableInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/inspections"
    }

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(MultipleExpectChainableInspection::class.java)
    }

    fun testHasMultipleExpectCall() {
        myFixture.configureByFile("MultipleExpectCall.php")

        myFixture.checkHighlighting()
    }

    fun testSingleExpectCall() {
        myFixture.configureByFile("SingleExpectCall.php")

        myFixture.checkHighlighting()
    }

    fun testReplacesMultipleExpectCallToChain() {
        myFixture.configureByFile("MultipleExpectCall.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().first().run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCall.after.php")
    }

    fun testHasExpectCallsWithOtherStatementsBetween() {
        myFixture.configureByFile("ExpectCallsWithOtherStatementsBetween.php")

        myFixture.checkHighlighting()
    }

    fun testHasMultipleExpectCallsWithOtherStatementsBetween() {
        myFixture.configureByFile("MultipleExpectCallsWithOtherStatementsBetween.php")

        myFixture.checkHighlighting()
    }

    fun testReplaceMultipleExpectCalls() {
        myFixture.configureByFile("MultipleExpectCallsWithOtherStatementsBetween.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().first().run { myFixture.launchAction(this) }
        myFixture.getAllQuickFixes().last().run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCallsWithOtherStatementsBetween.after.php")
    }

    fun testReplaceCanBeCalledOnFirstStatement() {
        myFixture.configureByFile("MultipleExpectCall.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().first().run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCall.after.php")
    }

    fun testReplaceCanBeCalledOnLastStatement() {
        myFixture.configureByFile("MultipleExpectCall.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().last().run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("MultipleExpectCall.after.php")
    }

    fun testReplaceCanBeCalledOnSecondaryStatement() {
        myFixture.configureByFile("ManyExpectCall.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes()[3].run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("ManyExpectCall.after.php")
    }
}

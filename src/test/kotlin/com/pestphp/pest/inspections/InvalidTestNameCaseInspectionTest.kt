package com.pestphp.pest.inspections

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/inspections")
class InvalidTestNameCaseInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/inspections"
    }

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(InvalidTestNameCaseInspection::class.java)
    }

    fun testInvalidTestNameCase() {
        myFixture.configureByFile("InvalidTestNameCase.php")

        myFixture.checkHighlighting()
    }

    fun testValidTestNameCase() {
        myFixture.configureByFile("ValidTestNameCase.php")

        myFixture.checkHighlighting()
    }

    fun testReplacesInvalidTestNameCase() {
        myFixture.configureByFile("InvalidTestNameCase.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().forEach { myFixture.launchAction(it) }

        myFixture.checkResultByFile("InvalidTestNameCase.after.php")
    }

    fun testValidTestNameWhenWrongCasingOnSomeWords() {
        myFixture.configureByFile("ValidTestNameWhenWrongCasingOnOneWord.php")

        myFixture.checkHighlighting()
    }

    fun testValidTestNameCaseInHigherOrderTest() {
        myFixture.configureByFile("ValidHigherOrderTestNameCase.php")

        myFixture.checkHighlighting()
    }
}

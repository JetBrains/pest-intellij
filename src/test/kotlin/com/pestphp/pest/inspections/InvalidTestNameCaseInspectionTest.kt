package com.pestphp.pest.inspections

import com.pestphp.pest.PestLightCodeFixture

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
}

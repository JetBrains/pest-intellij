package com.pestphp.pest.features.datasets

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/../resources/com/pestphp/pest/features/datasets")
class InvalidDatasetNameCaseInspectionTest : PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/features/datasets"

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(InvalidDatasetNameCaseInspection::class.java)
    }

    fun testInvalidDatasetNameCase() {
        myFixture.configureByFile("InvalidDatasetNameCase.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().forEach { myFixture.launchAction(it) }

        myFixture.checkResultByFile("InvalidDatasetNameCase.after.php")
    }

    fun testValidDatasetNameCase() {
        myFixture.configureByFile("ValidDatasetNameCase.php")

        myFixture.checkHighlighting()
    }
}
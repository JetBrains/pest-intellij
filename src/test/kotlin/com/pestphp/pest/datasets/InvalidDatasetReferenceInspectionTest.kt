package com.pestphp.pest.datasets

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/datasets")
class InvalidDatasetReferenceInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/datasets"
    }

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(InvalidDatasetReferenceInspection::class.java)
    }

    fun testHasInvalidDatasetName() {
        myFixture.configureByFile("InvalidDatasetTest.php")

        myFixture.checkHighlighting()
    }

    fun testHasValidDataset() {
        myFixture.configureByFile("DatasetTest.php")

        myFixture.checkHighlighting()
    }
}

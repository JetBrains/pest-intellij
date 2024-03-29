package com.pestphp.pest.features.datasets

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("/com/pestphp/pest/features/datasets")
class InvalidDatasetReferenceInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/features/datasets"
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

    fun testHasWithStatementWithNoArgs() {
        myFixture.configureByFile("DatasetNoArgsTest.php")

        myFixture.checkHighlighting()
    }
}

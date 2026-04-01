package com.pestphp.pest.features.datasets

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/../resources/com/pestphp/pest/features/datasets")
class InvalidDatasetReferenceInspectionTest : PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/features/datasets"

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

    fun testHasInvalidDatasetInDescribeBlock() {
        myFixture.configureByFile("InvalidDatasetInDescribeBlockTest.php")

        myFixture.checkHighlighting()
    }

    fun testDatasetInsideDescribeBlock() {
        myFixture.configureByFile("DatasetInsideDescribeBlockTest.php")

        myFixture.checkHighlighting()
    }
}

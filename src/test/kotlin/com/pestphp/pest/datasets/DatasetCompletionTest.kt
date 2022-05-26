package com.pestphp.pest.datasets

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/datasets")
class DatasetCompletionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/datasets"
    }

    fun testCanCompleteDatasetInSameFile() {
        myFixture.configureByFile("DatasetTest.php")

        assertCompletion("dojos")
    }

    fun testCanCompleteDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("DatasetTest.php")

        assertCompletion("dojos", "stances")
    }
}
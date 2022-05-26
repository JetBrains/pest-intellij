package com.pestphp.pest.datasets

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/datasets")
class DatasetCompletionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/datasets"
    }

    fun testCanCompleteDatasetInSameFile() {
        myFixture.configureByFile("AutocompleteDatasetTest.php")

        assertCompletion("dojos")
    }

    fun testCanCompleteDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("AutocompleteDatasetTest.php")

        assertCompletion("dojos", "stances")
    }

    fun testCannotCompleteDatasetOnNonPestTest() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("DatasetOnNonPestTestCompletion.php")

        assertNoCompletion()
    }
}

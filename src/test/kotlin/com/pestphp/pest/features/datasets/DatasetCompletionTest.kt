package com.pestphp.pest.features.datasets

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("/com/pestphp/pest/features/datasets")
class DatasetCompletionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/features/datasets"
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

    fun testCanCompleteDatasetInDescribeBlock() {
        myFixture.configureByFile("DatasetInDescribeBlockCompletion.php")

        assertCompletion("some_numbers")
    }
}

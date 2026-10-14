package com.pestphp.pest.features.datasets

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/datasets")
class DatasetCompletionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/datasets")
    }

    @Test
    fun testCanCompleteDatasetInSameFile() {
        myFixture.configureByFile("AutocompleteDatasetTest.php")

        PestTestUtil.assertCompletion(myFixture, "dojos")
    }

    @Test
    fun testCanCompleteDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("AutocompleteDatasetTest.php")

        PestTestUtil.assertCompletion(myFixture, "dojos", "stances")
    }

    @Test
    fun testCannotCompleteDatasetOnNonPestTest() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("DatasetOnNonPestTestCompletion.php")

        PestTestUtil.assertNoCompletion(myFixture)
    }

    @Test
    fun testCanCompleteDatasetInDescribeBlock() {
        myFixture.configureByFile("DatasetInDescribeBlockCompletion.php")

        PestTestUtil.assertCompletion(myFixture, "some_numbers")
    }
}

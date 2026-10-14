package com.pestphp.pest.features.datasets

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/datasets")
class DatasetReferenceTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/datasets")
    }

    @Test
    fun testReferenceToDatasetInSameFile() {
        val file = myFixture.configureByFile("DatasetReference.php")

        val (isPestDataset, datasetName) = runReadActionBlocking {
            val caretElement = file.findElementAt(myFixture.caretOffset) ?: fail("No element")
            val datasetReference = caretElement.parent.references.filterIsInstance<DatasetReference>().firstOrNull() ?: fail("No reference")
            val resolved = datasetReference.resolve() as? FunctionReferenceImpl ?: fail("No function")

            resolved.isPestDataset() to resolved.getPestDatasetName()
        }

        assertTrue(isPestDataset)
        assertEquals("dojos", datasetName)
    }

    @Test
    fun testReferenceDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        val file = myFixture.configureByFile("SharedDatasetReference.php")

        val (isPestDataset, datasetName) = runReadActionBlocking {
            val caretElement = file.findElementAt(myFixture.caretOffset) ?: fail("No element")
            val datasetReference = caretElement.parent.references.filterIsInstance<DatasetReference>().firstOrNull() ?: fail("No reference")
            val resolved = datasetReference.resolve() as? FunctionReferenceImpl ?: fail("No function")

            resolved.isPestDataset() to resolved.getPestDatasetName()
        }

        assertTrue(isPestDataset)
        assertEquals("stances", datasetName)
    }

    @Test
    fun testDoubleWith() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("DoubleWithDatasetReference.php")

        PestTestUtil.assertCompletion(myFixture, "stances")
    }

    @Test
    fun testNotDataset() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("NotDatasetReference.php")

        PestTestUtil.assertNoCompletion(myFixture)
    }

    @Test
    fun testCannotGoToDatasetInNonPestTest() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        val file = myFixture.configureByFile("DatasetOnNonPestTest.php")

        val datasetReference = runReadActionBlocking {
            val caretElement = file.findElementAt(myFixture.caretOffset) ?: fail("No element")
            caretElement.parent.references.filterIsInstance<DatasetReference>()
        }

        assertEquals(0, datasetReference.size)
    }

    @Test
    fun testReferenceToDatasetInDescribeBlock() {
        val file = myFixture.configureByFile("DatasetInDescribeBlockReference.php")

        val (isPestDataset, datasetName) = runReadActionBlocking {
            val caretElement = file.findElementAt(myFixture.caretOffset) ?: fail("No element")
            val datasetReference = caretElement.parent.references.filterIsInstance<DatasetReference>().firstOrNull() ?: fail("No reference")
            val resolved = datasetReference.resolve() as? FunctionReferenceImpl ?: fail("No function")

            resolved.isPestDataset() to resolved.getPestDatasetName()
        }

        assertTrue(isPestDataset)
        assertEquals("some_numbers", datasetName)
    }
}

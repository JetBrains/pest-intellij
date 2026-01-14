package com.pestphp.pest.features.datasets

import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("/com/pestphp/pest/features/datasets")
class DatasetReferenceTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/features/datasets"
    }

    fun testReferenceToDatasetInSameFile() {
        val file = myFixture.configureByFile("DatasetReference.php")

        val caretElement = file.findElementAt(myFixture.caretOffset) ?: return fail("No element")
        val datasetReference = caretElement.parent.references.filterIsInstance<DatasetReference>().firstOrNull() ?: return fail("No reference")
        val resolved = datasetReference.resolve() as? FunctionReferenceImpl ?: return fail("No function")

        assertTrue(resolved.isPestDataset())
        assertEquals("dojos", resolved.getPestDatasetName())
    }

    fun testReferenceDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        val file = myFixture.configureByFile("SharedDatasetReference.php")

        val caretElement = file.findElementAt(myFixture.caretOffset) ?: return fail("No element")
        val datasetReference = caretElement.parent.references.filterIsInstance<DatasetReference>().firstOrNull() ?: return fail("No reference")
        val resolved = datasetReference.resolve() as? FunctionReferenceImpl ?: return fail("No function")

        assertTrue(resolved.isPestDataset())
        assertEquals("stances", resolved.getPestDatasetName())
    }

    fun testDoubleWith() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("DoubleWithDatasetReference.php")

        assertCompletion("stances")
    }

    fun testNotDataset() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("NotDatasetReference.php")

        assertNoCompletion()
    }

    fun testCannotGoToDatasetInNonPestTest() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        val file = myFixture.configureByFile("DatasetOnNonPestTest.php")

        val caretElement = file.findElementAt(myFixture.caretOffset) ?: return fail("No element")
        val datasetReference = caretElement.parent.references.filterIsInstance<DatasetReference>()

        assertSize(0, datasetReference)
    }

    fun testReferenceToDatasetInDescribeBlock() {
        val file = myFixture.configureByFile("DatasetInDescribeBlockReference.php")

        val caretElement = file.findElementAt(myFixture.caretOffset) ?: return fail("No element")
        val datasetReference = caretElement.parent.references.filterIsInstance<DatasetReference>().firstOrNull() ?: return fail("No reference")
        val resolved = datasetReference.resolve() as? FunctionReferenceImpl ?: return fail("No function")

        assertTrue(resolved.isPestDataset())
        assertEquals("some_numbers", resolved.getPestDatasetName())
    }
}

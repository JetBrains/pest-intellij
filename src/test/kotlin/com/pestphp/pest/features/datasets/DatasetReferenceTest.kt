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

        val caretElement = file.findElementAt(myFixture.caretOffset)!!
        val datasetReference = caretElement.parent.references[0] as DatasetReference
        val resolved = datasetReference.resolve() as? FunctionReferenceImpl

        assertNotNull(datasetReference)
        assertTrue(resolved.isPestDataset())
        assertEquals("dojos", resolved!!.getPestDatasetName())
    }

    fun testReferenceDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        val file = myFixture.configureByFile("SharedDatasetReference.php")

        val caretElement = file.findElementAt(myFixture.caretOffset)!!
        val datasetReference = caretElement.parent.references[0] as DatasetReference
        val resolved = datasetReference.resolve() as? FunctionReferenceImpl

        assertNotNull(datasetReference)
        assertTrue(resolved.isPestDataset())
        assertEquals("stances", resolved!!.getPestDatasetName())
    }

    fun testCannotGoToDatasetInNonPestTest() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        val file = myFixture.configureByFile("DatasetOnNonPestTest.php")

        val caretElement = file.findElementAt(myFixture.caretOffset)!!
        val datasetReference = caretElement.parent.references

        assertSize(0, datasetReference)
    }
}

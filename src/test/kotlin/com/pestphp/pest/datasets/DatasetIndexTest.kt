package com.pestphp.pest.datasets

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.TestDataPath
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.PestLightCodeFixture
import junit.framework.TestCase

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/datasets")
class DatasetIndexTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/datasets"
    }

    fun testDatasetIsIndexed() {
        myFixture.copyFileToProject(
            "Datasets.php",
            "/tests/Datasets/Datasets.php"
        )

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = fileBasedIndex.getValues(
            DatasetIndex.key,
            "/src/tests/Datasets/Datasets.php",
            GlobalSearchScope.projectScope(project)
        ).flatten()

        assertSize(1, values)
        TestCase.assertEquals(
            listOf("stances"),
            values
        )
    }

    fun testDatasetIsNotIndexedWhenOutsideDatasetFolder() {
        val virtualFile = myFixture.copyFileToProject(
            "Datasets.php",
            "/tests/Datasets.php"
        )

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexData = fileBasedIndex.getFileData(
            DatasetIndex.key,
            virtualFile,
            project
        )

        assertEquals(0, indexData.count())
    }
}
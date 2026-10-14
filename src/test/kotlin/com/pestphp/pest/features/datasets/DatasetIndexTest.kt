package com.pestphp.pest.features.datasets

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.IndexingTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/datasets")
class DatasetIndexTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/datasets")
        IndexingTestUtil.waitUntilIndexesAreReady(myFixture.project)
    }

    @Test
    fun testDatasetIsIndexed() {
        val virtualFile = myFixture.copyFileToProject(
            "Datasets.php",
            "/tests/Datasets/Datasets.php"
        )

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = runReadActionBlocking {
            fileBasedIndex.getValues(
              key,
              virtualFile.path,
              GlobalSearchScope.projectScope(myFixture.project)
            ).flatten()
        }

        assertEquals(1, values.size)
        assertEquals(
            listOf("stances"),
            values
        )
    }

    @Test
    fun testDatasetIsNotIndexedWhenOutsideDatasetFolder() {
        val virtualFile = myFixture.copyFileToProject(
            "Datasets.php",
            "/tests/Datasets.php"
        )

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexData = runReadActionBlocking {
            fileBasedIndex.getFileData(
              key,
              virtualFile,
              myFixture.project
            )
        }

        assertEquals(0, indexData.count())
    }

    @Test
    fun testDatasetInsideDescribeBlockIsIndexed() {
        val virtualFile = myFixture.copyFileToProject(
            "DatasetInDescribeBlock.php",
            "/tests/Datasets/DatasetInDescribeBlock.php"
        )

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = runReadActionBlocking {
            fileBasedIndex.getValues(
                key,
                virtualFile.path,
                GlobalSearchScope.projectScope(myFixture.project)
            ).flatten()
        }

        assertEquals(2, values.size)
        assertTrue(values.containsAll(listOf("top_level", "inside_describe")), "$values does not contain all of [top_level, inside_describe]")
    }
}
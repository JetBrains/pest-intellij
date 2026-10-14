package com.pestphp.pest.indexers

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.IndexingTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/indexers/PestTestIndexTest")
class PestTestIndexTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/indexers/PestTestIndexTest")
        IndexingTestUtil.waitUntilIndexesAreReady(myFixture.project)
    }

    @Test
    fun testPestTestFileIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithPestTest.php", "tests/FileWithPestTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = runReadActionBlocking {
            fileBasedIndex.getAllKeys(key, myFixture.project).filter {
                fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(myFixture.project)).isNotEmpty()
            }
        }

        assertTrue(indexKeys.contains(virtualFile.path), "$indexKeys does not contain ${virtualFile.path}")
    }

    @Test
    fun testPhpFileIsNotIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithoutPestTest.php", "tests/FileWithoutPestTest.php")

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = runReadActionBlocking {
            fileBasedIndex.getAllKeys(key, myFixture.project).filter {
                fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(myFixture.project)).isNotEmpty()
            }
        }

        assertFalse(indexKeys.contains(virtualFile.path), "$indexKeys contains ${virtualFile.path}")
    }

    @Test
    fun testPestTestFileWithTodoIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithPestTodosTest.php", "tests/FileWithPestTodosTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = runReadActionBlocking {
            fileBasedIndex.getAllKeys(key, myFixture.project).filter {
                fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(myFixture.project)).isNotEmpty()
            }
        }

        assertTrue(indexKeys.contains(virtualFile.path), "$indexKeys does not contain ${virtualFile.path}")
    }

    @Test
    fun testPestTestFileOutsideTestDirectoryIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithPestTest.php", "anywhere-but-not-te-st/FileWithPestTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = runReadActionBlocking {
            fileBasedIndex.getAllKeys(key, myFixture.project).filter {
                fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(myFixture.project)).isNotEmpty()
            }
        }

        assertTrue(indexKeys.contains(virtualFile.path), "$indexKeys does not contain ${virtualFile.path}")
    }

    @Test
    fun testDescribeBlockTestsAreIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithDescribeBlockTest.php", "tests/FileWithDescribeBlockTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val testNames = runReadActionBlocking {
            fileBasedIndex.getValues(key, virtualFile.path, GlobalSearchScope.allScope(myFixture.project))
                .flatten()
        }

        val expectedTestNames = listOf("`sum` → ", "`sum` → adds numbers", "`sum` → it handles zero")
        assertTrue(testNames.containsAll(expectedTestNames), "$testNames does not contain all of $expectedTestNames")
    }
}

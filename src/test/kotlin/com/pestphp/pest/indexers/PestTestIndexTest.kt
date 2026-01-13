package com.pestphp.pest.indexers

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.TestDataPath
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/indexers/PestTestIndexTest")
class PestTestIndexTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/indexers/PestTestIndexTest"
    }

    fun testPestTestFileIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithPestTest.php", "tests/FileWithPestTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = fileBasedIndex.getAllKeys(key, project).filter {
            fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(project)).isNotEmpty()
        }

        assertContainsElements(indexKeys, "/src/tests/FileWithPestTest.php")
    }

    fun testPhpFileIsNotIndexed() {
        myFixture.copyFileToProject("FileWithoutPestTest.php", "tests/FileWithoutPestTest.php")

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = fileBasedIndex.getAllKeys(key, project).filter {
            fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(project)).isNotEmpty()
        }

        assertDoesntContain(indexKeys, "/src/tests/FileWithoutPestTest.php")
    }

    fun testPestTestFileWithTodoIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithPestTodosTest.php", "tests/FileWithPestTodosTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = fileBasedIndex.getAllKeys(key, project).filter {
            fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(project)).isNotEmpty()
        }

        assertContainsElements(indexKeys, "/src/tests/FileWithPestTodosTest.php")
    }

    fun testPestTestFileOutsideTestDirectoryIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithPestTest.php", "anywhere-but-not-te-st/FileWithPestTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = fileBasedIndex.getAllKeys(key, project).filter {
            fileBasedIndex.getContainingFiles(key, it, GlobalSearchScope.allScope(project)).isNotEmpty()
        }

        assertContainsElements(indexKeys, "/src/anywhere-but-not-te-st/FileWithPestTest.php")
    }

    fun testDescribeBlockTestsAreIndexed() {
        val virtualFile = myFixture.copyFileToProject("FileWithDescribeBlockTest.php", "tests/FileWithDescribeBlockTest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val fileBasedIndex = FileBasedIndex.getInstance()

        val testNames = fileBasedIndex.getValues(key, "/src/tests/FileWithDescribeBlockTest.php", GlobalSearchScope.allScope(project))
            .flatten()

        assertContainsElements(testNames, "`sum` → ", "`sum` → adds numbers", "`sum` → it handles zero")
    }
}

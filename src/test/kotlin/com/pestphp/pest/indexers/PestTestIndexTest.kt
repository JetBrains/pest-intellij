package com.pestphp.pest.indexers

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.PestLightCodeFixture

class PestTestIndexTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/indexers/PestTestIndexTest"
    }

    fun testPestTestFileIsIndexed() {
        myFixture.copyFileToProject("FileWithPestTest.php", "tests/FileWithPestTest.php")

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = fileBasedIndex.getAllKeys(PestTestIndex.key, project).filter {
            fileBasedIndex.getContainingFiles(PestTestIndex.key, it, GlobalSearchScope.allScope(project)).isNotEmpty()
        }

        assertContainsElements(indexKeys, "FileWithPestTest.php")
    }

    fun testPhpFileIsNotIndexed() {
        myFixture.copyFileToProject("FileWithoutPestTest.php", "tests/FileWithoutPestTest.php")

        val fileBasedIndex = FileBasedIndex.getInstance()

        val indexKeys = fileBasedIndex.getAllKeys(PestTestIndex.key, project).filter {
            fileBasedIndex.getContainingFiles(PestTestIndex.key, it, GlobalSearchScope.allScope(project)).isNotEmpty()
        }

        assertDoesntContain(indexKeys, "FileWithoutPestTest.php")
    }
}

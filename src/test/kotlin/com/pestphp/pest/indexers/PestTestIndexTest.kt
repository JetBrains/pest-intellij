package com.pestphp.pest.indexers

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.tests.PestLightCodeFixture
import junit.framework.TestCase

class PestTestIndexTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
        return "src/test/resources/com/pestphp/pest/indexers/PestTestIndexTest"
    }

    fun testPestTestFileIsIndexed() {
        val file = myFixture.configureByFile("FileWithPestTest.php")

        val indexes = FileBasedIndex.getInstance().getAllKeys(PestTestIndex.key, project)

        val files = FileBasedIndex.getInstance().getContainingFiles(
            PestTestIndex.key,
            indexes.first(),
            GlobalSearchScope.projectScope(project)
        )

        assertNotNull(file.virtualFile)
        assertContainsElements(files, file.virtualFile)
    }

    fun testPhpFileIsNotIndexed() {
        val file = myFixture.configureByFile("FileWithoutPestTest.php")

        val indexes = FileBasedIndex.getInstance().getAllKeys(PestTestIndex.key, project)

        val files = FileBasedIndex.getInstance().getContainingFiles(
            PestTestIndex.key,
            indexes.first(),
            GlobalSearchScope.projectScope(project)
        )

        assertDoesntContain(files, file.virtualFile)
    }
}

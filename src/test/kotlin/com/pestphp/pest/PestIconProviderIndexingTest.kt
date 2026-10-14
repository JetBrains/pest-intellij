package com.pestphp.pest

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.util.Iconable.ICON_FLAG_VISIBILITY
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.IndexingTestUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.swing.Icon

@TestApplication
class PestIconProviderIndexingTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
        IndexingTestUtil.waitUntilIndexesAreReady(myFixture.project)
    }

    private val fileBasedIndex: FileBasedIndexImpl
        get() = FileBasedIndex.getInstance() as FileBasedIndexImpl

    @Test
    fun testIconOfScriptIndexesNoOtherChangedFile() {
        val script = myFixture.copyFileToProject("SimpleScript.php", "src/SimpleScript.php")
        val other = myFixture.copyFileToProject("SimpleScript.php", "src/Other.php")
        runReadActionBlocking { fileBasedIndex.forceUpdateProjectInTest(myFixture.project) }

        changeOnDisk(other)

        assertNull(getIcon(script))
        assertFalse(fileBasedIndex.isFileUpToDate(other), "the icon lookup indexed ${other.name}")
    }

    @Test
    fun testIconOfDatasetIndexesNoOtherChangedFile() {
        val dataset = myFixture.copyFileToProject("Dataset.php", "tests/Datasets/Dataset.php")
        val other = myFixture.copyFileToProject("SimpleScript.php", "src/Other.php")
        runReadActionBlocking { fileBasedIndex.forceUpdateProjectInTest(myFixture.project) }

        changeOnDisk(other)

        assertEquals(PestIcons.Dataset, getIcon(dataset))
        assertFalse(fileBasedIndex.isFileUpToDate(other), "the icon lookup indexed ${other.name}")
    }

    private fun changeOnDisk(file: VirtualFile) {
        WriteAction.runAndWait<Throwable> { VfsUtil.saveText(file, VfsUtil.loadText(file) + "\n// changed\n") }
        fileBasedIndex.changedFilesCollector.ensureUpToDate()
        assertFalse(fileBasedIndex.isFileUpToDate(file), "${file.name} should wait for indexing")
    }

    private fun getIcon(file: VirtualFile): Icon? = runReadActionBlocking {
        PestIconProvider().getIcon(file, ICON_FLAG_VISIBILITY, myFixture.project)
    }
}

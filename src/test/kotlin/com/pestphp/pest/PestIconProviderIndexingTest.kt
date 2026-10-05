package com.pestphp.pest

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.util.Iconable.ICON_FLAG_VISIBILITY
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexImpl

class PestIconProviderIndexingTest : PestLightCodeFixture() {
    private val fileBasedIndex: FileBasedIndexImpl
        get() = FileBasedIndex.getInstance() as FileBasedIndexImpl

    fun testIconOfScriptIndexesNoOtherChangedFile() {
        val script = myFixture.copyFileToProject("SimpleScript.php", "src/SimpleScript.php")
        val other = myFixture.copyFileToProject("SimpleScript.php", "src/Other.php")
        fileBasedIndex.forceUpdateProjectInTest(project)

        changeOnDisk(other)

        assertNull(PestIconProvider().getIcon(script, ICON_FLAG_VISIBILITY, project))
        assertFalse("the icon lookup indexed ${other.name}", fileBasedIndex.isFileUpToDate(other))
    }

    fun testIconOfDatasetIndexesNoOtherChangedFile() {
        val dataset = myFixture.copyFileToProject("Dataset.php", "tests/Datasets/Dataset.php")
        val other = myFixture.copyFileToProject("SimpleScript.php", "src/Other.php")
        fileBasedIndex.forceUpdateProjectInTest(project)

        changeOnDisk(other)

        assertEquals(PestIcons.Dataset, PestIconProvider().getIcon(dataset, ICON_FLAG_VISIBILITY, project))
        assertFalse("the icon lookup indexed ${other.name}", fileBasedIndex.isFileUpToDate(other))
    }

    private fun changeOnDisk(file: VirtualFile) {
        WriteAction.runAndWait<Throwable> { VfsUtil.saveText(file, VfsUtil.loadText(file) + "\n// changed\n") }
        fileBasedIndex.changedFilesCollector.ensureUpToDate()
        assertFalse("${file.name} should wait for indexing", fileBasedIndex.isFileUpToDate(file))
    }
}

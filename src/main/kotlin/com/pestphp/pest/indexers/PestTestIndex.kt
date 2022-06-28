package com.pestphp.pest.indexers

import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.psi.stubs.indexes.StringSetDataExternalizer
import com.pestphp.pest.*
import gnu.trove.THashMap

/**
 * Indexes all pest test files with the following key-value store
 * `path/pest-test-file-name => ['it test', 'it should work']`
 */
class PestTestIndex : FileBasedIndexExtension<String, Set<String>>() {
    override fun getName(): ID<String, Set<String>> {
        return key
    }

    override fun getVersion(): Int {
        return 3
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    override fun getIndexer(): DataIndexer<String, Set<String>, FileContent> {
        return DataIndexer { inputData ->
            val file = inputData.psiFile

            if (!file.isPestTestFile()) {
                return@DataIndexer mapOf()
            }

            val map = THashMap<String, Set<String>>()
            map[file.realPath] = file.getPestTests()
                .mapNotNull { it.getPestTestName() }
                .toSet()
            return@DataIndexer map
        }
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return PhpTestFolderInputFilter()
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<Set<String>> {
        return StringSetDataExternalizer.INSTANCE
    }

    companion object {
        val key = ID.create<String, Set<String>>("php.pest")
    }
}

package com.pestphp.pest.indexers

import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.PhpFileType
import com.pestphp.pest.expectExtends
import com.pestphp.pest.extendName

class ExpectExtendIndex : ScalarIndexExtension<String>() {
    override fun getName(): ID<String, Void> {
        return key
    }

    override fun getVersion(): Int {
        return 0
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> {
        return DataIndexer { inputData ->
            val file = inputData.psiFile
            val expectExtends = file.expectExtends

            if (expectExtends.isEmpty()) {
                return@DataIndexer mapOf()
            }

            return@DataIndexer expectExtends
                .mapNotNull { it.extendName }
                .associateWith { null }
        }
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    companion object {
        val key = ID.create<String, Void>("php.pest.expect.extend")
    }
}

package com.pestphp.pest.completion

import com.intellij.openapi.project.guessProjectDir
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.intellij.util.io.VoidDataExternalizer
import com.jetbrains.php.lang.PhpFileType
import com.pestphp.pest.getAllBeforeThisAssignments

class ThisFieldsIndex : FileBasedIndexExtension<String, Void?>() {
    private val emptyMap = HashMap<String, Void?>()

    override fun getName(): ID<String, Void?> {
        return KEY
    }

    override fun getIndexer(): DataIndexer<String, Void?, FileContent> {
        return DataIndexer { fileContent ->
            val path = fileContent.file.path.removePrefix(fileContent.project.guessProjectDir()?.path
                ?: return@DataIndexer emptyMap)

            if (!path.startsWith("/tests/")) return@DataIndexer emptyMap

            fileContent.psiFile.getAllBeforeThisAssignments().map {
                it.variable?.name!! to null
            }.toMap()
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<Void?> {
        return VoidDataExternalizer.INSTANCE
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return FileBasedIndex.InputFilter { file ->
            file.fileType === PhpFileType.INSTANCE
        }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    override fun getVersion(): Int {
        return 1
    }

    companion object {
        val KEY = ID.create<String, Void>("com.pestphp.this_fields_index")
    }
}

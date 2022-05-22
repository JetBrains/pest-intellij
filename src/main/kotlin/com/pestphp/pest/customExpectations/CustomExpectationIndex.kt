package com.pestphp.pest.customExpectations


import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.customExpectations.externalizers.ListDataExternalizer
import com.pestphp.pest.customExpectations.externalizers.MethodDataExternalizer
import com.pestphp.pest.customExpectations.generators.Method
import com.pestphp.pest.realPath

class CustomExpectationIndex : FileBasedIndexExtension<String, List<Method>>() {
    companion object {
        val key = ID.create<String, List<Method>>("php.pest.custom_expectations")
    }

    override fun getName(): ID<String, List<Method>> {
        return key
    }

    override fun getVersion(): Int {
        return 3
    }

    override fun getIndexer(): DataIndexer<String, List<Method>, FileContent> {
        return DataIndexer { inputData ->
            val file = inputData.psiFile

            if (file !is PhpFile) {
                return@DataIndexer mapOf()
            }

            val customExpectations = file
                .customExpects
                .mapNotNull {
                    it.toMethod()
                }

            val publisher = file.project.messageBus.syncPublisher(CustomExpectationNotifier.TOPIC)
            publisher.changedExpectation(
                file,
                customExpectations
            )

            if (customExpectations.isEmpty()) {
                return@DataIndexer mapOf()
            }

            mapOf(
                file.realPath to customExpectations
            )
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<List<Method>> {
        return ListDataExternalizer(MethodDataExternalizer.INSTANCE)
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            override fun acceptInput(file: VirtualFile): Boolean {
                return true
            }
        }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }
}
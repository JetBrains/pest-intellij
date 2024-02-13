package com.pestphp.pest.features.customExpectations

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.stubs.indexes.PhpDepthLimitedRecursiveElementVisitor
import com.jetbrains.php.lang.psi.stubs.indexes.PhpInvokeCallsOffsetsIndex.IntArrayExternalizer
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

val KEY = ID.create<String, IntList>("php.pest.custom_expectations")

class CustomExpectationIndex : FileBasedIndexExtension<String, IntList>() {

    override fun getName(): ID<String, IntList> {
        return KEY
    }

    override fun getVersion(): Int {
        return 6
    }

    override fun getIndexer(): DataIndexer<String, IntList, FileContent> {
        return DataIndexer { inputData ->
            val file = inputData.psiFile
            val map: MutableMap<String, IntList> = mutableMapOf()
            if (file is PhpFile) {
                file.accept(object : PhpDepthLimitedRecursiveElementVisitor() {
                    override fun visitPhpMethodReference(reference: MethodReference) {
                        if (reference is MethodReferenceImpl && reference.isPestExtendReference()) {
                            reference.extendName?.let {
                                if (it !in map) {
                                    map[it] = IntArrayList()
                                }
                                map[it]!!.add(reference.parameters[0].textOffset + 1)
                            }
                        }
                    }
                })
            }
            return@DataIndexer map
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<IntList> {
        return IntArrayExternalizer.INSTANCE
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            override fun acceptInput(file: VirtualFile): Boolean {
                return !isPestStubFile(file)
            }
        }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    private fun isPestStubFile(file: VirtualFile): Boolean {
        val path = file.path
        return path.contains("vendor") && path.contains("pestphp") && path.contains("stubs")
    }
}

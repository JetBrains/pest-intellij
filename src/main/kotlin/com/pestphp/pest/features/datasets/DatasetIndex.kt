package com.pestphp.pest.features.datasets

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.features.customExpectations.externalizers.ListDataExternalizer
import com.pestphp.pest.getRootPhpPsiElements
import com.pestphp.pest.realPath

val key = ID.create<String, List<String>>("php.pest.datasets")

/**
 * Indexes all pest datasets with the following key value store
 *  `path/datasets/file => ['my-dataset', 'my-other-dataset']
 */
class DatasetIndex : FileBasedIndexExtension<String, List<String>>() {
    override fun getName(): ID<String, List<String>> {
        return key
    }

    override fun getVersion(): Int {
        return 2
    }

    override fun getIndexer(): DataIndexer<String, List<String>, FileContent> {
        return DataIndexer { inputData ->
            val file = inputData.psiFile

            if (file !is PhpFile) {
                return@DataIndexer mapOf()
            }

            val datasets = file
                .getRootPhpPsiElements()
                .filter { it.isPestDataset() }
                .filterIsInstance<FunctionReferenceImpl>()
                .mapNotNull { it.getPestDatasetName() }

            if (datasets.isEmpty()) {
                return@DataIndexer mapOf()
            }

            mapOf(
                file.realPath to datasets
            )
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<List<String>> {
        return ListDataExternalizer(EnumeratorStringDescriptor.INSTANCE)
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
      return object : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
        override fun acceptInput(file: VirtualFile): Boolean {
          return super.acceptInput(file) && file.parent.path.endsWith("/Datasets")
        }
      }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }
}
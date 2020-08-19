package com.pestphp.pest.indexers

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.PhpFileType
import com.pestphp.pest.isPestTestFile
import gnu.trove.THashMap

class PestTestIndex : ScalarIndexExtension<String>() {
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

            if (!file.isPestTestFile()) {
                return@DataIndexer mapOf()
            }

            val map = THashMap<String, Void>()
            map[file.name] = null
            return@DataIndexer map
        }
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            override fun acceptInput(file: VirtualFile): Boolean {
                if (file.path.contains(""".*?test.*?/.*\..*""".toRegex())) {
                    return true
                }

                return ProjectManager.getInstance().openProjects.any {
                    TestSourcesFilter.isTestSources(file, it)
                }
            }
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    companion object {
        val key = ID.create<String, Void>("php.pest")
    }
}

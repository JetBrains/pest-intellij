package com.pestphp.pest

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.jetbrains.php.lang.PhpFileType

open class PhpTestFolderInputFilter : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
    override fun acceptInput(file: VirtualFile): Boolean {
        return file.path.contains(""".*?test.*?/.*\..*""".toRegex());
    }
}
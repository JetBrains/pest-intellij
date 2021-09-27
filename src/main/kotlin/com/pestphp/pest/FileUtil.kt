package com.pestphp.pest

import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.php.lang.psi.PhpFile

/**
 * Takes care of getting the path of a file even if it's a light file.
 */
val PhpFile.realPath: String
    get() {
        var virtualFile = this.viewProvider.virtualFile
        if (virtualFile is LightVirtualFile) {
            virtualFile = virtualFile.originalFile
        }
        return virtualFile.path
    }
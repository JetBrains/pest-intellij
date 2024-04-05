package com.pestphp.pest

import com.intellij.psi.PsiFile
import com.intellij.testFramework.LightVirtualFile

/**
 * Takes care of getting the path of a file even if it's a light file.
 */
val PsiFile.realPath: String
    get() {
        var virtualFile = this.viewProvider.virtualFile
        if (virtualFile is LightVirtualFile && virtualFile.originalFile != null) {
            virtualFile = virtualFile.originalFile
        }
        return virtualFile.path
    }
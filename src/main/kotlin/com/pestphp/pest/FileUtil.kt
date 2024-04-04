package com.pestphp.pest

import com.intellij.openapi.vfs.originalFileOrSelf
import com.intellij.psi.PsiFile

/**
 * Takes care of getting the path of a file even if it's a light file.
 */
val PsiFile.realPath: String
    get() {
        return viewProvider.virtualFile.originalFileOrSelf().path
    }
package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.php.phpunit.codeGeneration.PhpNewTestAction
import com.jetbrains.php.testFramework.PhpTestCreateInfo

open class PestNewTestFromClassAction: PhpNewTestAction() {
    override fun getDefaultTestCreateInfo(project: Project, locationContext: VirtualFile?): PhpTestCreateInfo {
        return PestTestCreateInfo
    }
}
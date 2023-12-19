package com.pestphp.pest

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.php.phpunit.codeGeneration.PhpNewTestAction
import com.jetbrains.php.testFramework.PhpTestCreateInfo

class PestNewTestFromClassAction: PhpNewTestAction(PestBundle.messagePointer("action.Pest.New.File.text"),
                                                   PestBundle.messagePointer("ACTIONS_NEW_PEST_TEST_ACTION_DESCRIPTION"),
                                                   PestIcons.Logo) {
    override fun getDefaultTestCreateInfo(project: Project, locationContext: VirtualFile?): PhpTestCreateInfo {
        return PestTestCreateInfo
    }
}
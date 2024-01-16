package com.pestphp.pest.generateTest

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.testFramework.utils.vfs.getPsiFile
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.phpunit.codeGeneration.PhpNewTestDialog
import com.jetbrains.php.templates.PhpCreateFileFromTemplateDataProvider
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.PestNewTestFromClassAction
import com.pestphp.pest.PestTestCreateInfo

class PestNewTestFromClassActionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/generateTest"
    }

    private fun createTestFile(file: PsiFile, namespace: String): PsiFile {
        val selectedElement = PhpPsiUtil.findClass(file as PhpFile) { _ -> true }
        val testFile = MockPestNewTestFromClassAction.publicCreateFile(
            project,
            object : PhpNewTestDialog(project, null, file, PestTestCreateInfo, selectedElement) {
                override fun getNamespace(): String {
                    return namespace
                }

                override fun getSelectedClassMembers(): MutableSet<Method> {
                    return selectedElement?.methods?.toMutableSet() ?: mutableSetOf()
                }
            })
        assertNotNull(testFile)
        return testFile!!
    }

    private object MockPestNewTestFromClassAction : PestNewTestFromClassAction() {
        fun publicCreateFile(project: Project, dataProvider: PhpCreateFileFromTemplateDataProvider): PsiFile? {
            return super.createFile(project, dataProvider)
        }
    }

    fun testPestWithNamespace() {
        val fileWithClass = myFixture.copyFileToProject("testWithNamespace.php")
        val testFile = createTestFile(fileWithClass.getPsiFile(project), "a")
        myFixture.configureByText(testFile.getName(), testFile.getText())
        myFixture.checkResultByFile("testWithNamespace.after.php")
    }
}
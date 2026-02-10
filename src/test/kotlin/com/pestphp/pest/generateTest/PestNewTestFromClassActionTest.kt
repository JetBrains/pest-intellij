package com.pestphp.pest.generateTest

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.ex.temp.TempFileSystem
import com.intellij.psi.PsiFile
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.utils.vfs.getPsiFile
import com.intellij.util.PathUtil
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.phpunit.codeGeneration.PhpNewTestDialog
import com.jetbrains.php.roots.ui.PhpPsrDirectoryComboBox
import com.jetbrains.php.templates.PhpCreateFileFromTemplateDataProvider
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.PestNewTestFromClassAction
import com.pestphp.pest.PestTestCreateInfo

class PestNewTestFromClassActionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/generateTest"
    }

    private fun createTestFile(file: PsiFile, namespace: String): PsiFile {
        val dialog = getDialog(file, namespace)
        try {
            val testFile = MockPestNewTestFromClassAction.publicCreateFile(project, dialog)
            assertNotNull(testFile)
            return testFile!!
        } finally {
            dialog.disposeIfNeeded()
        }
    }

    private object MockPestNewTestFromClassAction : PestNewTestFromClassAction() {
        fun publicCreateFile(project: Project, dataProvider: PhpCreateFileFromTemplateDataProvider): PsiFile? {
            return super.createFile(project, dataProvider)
        }
    }

    private fun getDialog(file: PsiFile, namespace: String): PhpNewTestDialog {
        val phpClass = PhpPsiUtil.findClass(file as PhpFile) { _ -> true }
        return object : PhpNewTestDialog(project, file.containingDirectory, file, PestTestCreateInfo, phpClass) {
            override fun createDirectoryCombobox(): PhpPsrDirectoryComboBox {
                return object : PhpPsrDirectoryComboBox(project, testDirectoryProvider) {
                    override fun init(baseDir: VirtualFile, namespace: String) {
                        super.init(baseDir, namespace)
                        updateSuggestions(getNamespace())
                    }

                    override fun getExistingParent(): VirtualFile {
                        return findExistingParent(selectedPath) ?: PlatformTestUtil.getOrCreateProjectBaseDir(project)
                    }

                    private fun findExistingParent(path: String): VirtualFile? {
                        if (StringUtil.isEmpty(path)) {
                            return null
                        }
                        val directory = TempFileSystem.getInstance().findFileByPath(path)
                        return directory ?: findExistingParent(PathUtil.getParentPath(path))
                    }

                    override fun getBaseDirectory(): VirtualFile {
                        return myDirectoryCombobox.existingParent ?: super.getBaseDirectory()
                    }
                }
            }

            override fun getNamespace(): String {
                return namespace
            }

            override fun getSelectedClassMembers(): MutableSet<Method> {
                return phpClass?.methods?.toMutableSet() ?: mutableSetOf()
            }
        }
    }

    fun testPestWithNamespace() {
        val fileWithClass = myFixture.copyFileToProject("testWithNamespace.php")
        val testFile = createTestFile(fileWithClass.getPsiFile(project), "a")
        myFixture.configureByText(testFile.getName(), testFile.getText())
        myFixture.checkResultByFile("testWithNamespace.after.php")
    }
}
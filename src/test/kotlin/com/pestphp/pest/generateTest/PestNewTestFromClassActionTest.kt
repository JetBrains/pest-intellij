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
        val selectedElement = PhpPsiUtil.findClass(file as PhpFile) { _ -> true }
        val testFile = MockPestNewTestFromClassAction.publicCreateFile(
            project,
            object : PhpNewTestDialog(project, file.containingDirectory, file, PestTestCreateInfo, selectedElement) {
                override fun createUIComponents() {
                    super.createUIComponents()
                    myDirectoryCombobox = object : PhpPsrDirectoryComboBox(project, testDirectoryProvider) {
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
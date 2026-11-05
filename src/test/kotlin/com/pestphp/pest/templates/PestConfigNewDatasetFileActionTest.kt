package com.pestphp.pest.templates

import com.intellij.ide.IdeView
import com.intellij.ide.actions.TestDialogBuilder
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.testFramework.runInEdtAndWait
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.templates.PestConfigNewDatasetFileAction.Companion.PEST_SHARED_DATASET_TEMPLATE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PestConfigNewDatasetFileActionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
    }

    @Test
    fun `shared dataset creates the Datasets directory`() {
        val created = createSharedDataset(myFixture.tempDirFixture.findOrCreateDir("tests"), "users")
        assertEquals("tests/Datasets/Users.php", relativePath(created))
    }

    @Test
    fun `shared dataset goes to the existing Datasets directory`() {
        myFixture.tempDirFixture.findOrCreateDir("tests/Datasets")
        val created = createSharedDataset(myFixture.tempDirFixture.findOrCreateDir("tests"), "Users")
        assertEquals("tests/Datasets/Users.php", relativePath(created))
    }

    @Test
    fun `shared dataset from a nested directory goes to the Datasets directory of tests`() {
        val created = createSharedDataset(myFixture.tempDirFixture.findOrCreateDir("tests/Unit"), "users")
        assertEquals("tests/Datasets/Users.php", relativePath(created))
    }

    /**
     * Performs New Pest Dataset in [directory] and returns the file it selects.
     */
    private fun createSharedDataset(directory: VirtualFile, name: String): VirtualFile {
        var selected: VirtualFile? = null
        runInEdtAndWait {
            val psiDirectory = PsiManager.getInstance(myFixture.project).findDirectory(directory)!!
            val view = object : IdeView {
                override fun getDirectories(): Array<PsiDirectory> = arrayOf(psiDirectory)
                override fun getOrChooseDirectory(): PsiDirectory = psiDirectory
                override fun selectElement(element: PsiElement) {
                    selected = element.containingFile.virtualFile
                }
            }
            val dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, myFixture.project)
                .add(LangDataKeys.IDE_VIEW, view)
                .add(TestDialogBuilder.TestAnswers.KEY, TestDialogBuilder.TestAnswers(name, PEST_SHARED_DATASET_TEMPLATE))
                .build()
            val action = ActionManager.getInstance().getAction("Pest.New.Dataset")
            action.actionPerformed(TestActionEvent.createTestEvent(action, dataContext))
        }
        return selected ?: error("No file was created")
    }

    private fun relativePath(file: VirtualFile): String = file.path.removePrefix(myFixture.tempDirFixture.getFile("")!!.path + "/")
}

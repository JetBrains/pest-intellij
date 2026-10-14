package com.pestphp.pest

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.util.Iconable.ICON_FLAG_VISIBILITY
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiManager
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.swing.Icon

@TestApplication
class PestIconProviderTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
        myFixture.copyFileToProject("utilTests/SimpleTest.php", "tests/SimpleTest.php")
    }

    @Test
    fun testCanGetPestIconForPestFile() {
        val file = myFixture.configureByFile("tests/SimpleTest.php")

        assertEquals(
            PestIcons.File,
            getIcon(file.virtualFile),
        )
    }

    @Test
    fun testCanGetOtherIconForFileRedefiningPestFunction() {
        val virtualFile = myFixture.copyFileToProject(
            "PestTestRunLineMarkerProviderTest/PestItFunctionCallWithRedefinition.php",
            "tests/RedefinitionTest.php"
        )

        assertNull(
            getIcon(virtualFile)
        )
    }

    @Test
    fun testCanGetOtherIconForNonPestFile() {
        val file = myFixture.configureByFile("SimpleScript.php")

        assertNull(
            getIcon(file.virtualFile)
        )
    }

    @Test
    fun testCanGetPestIconForDatasetFile() {
        myFixture.copyFileToProject("Dataset.php", "/tests/Datasets/Dataset.php")
        val file = myFixture.configureByFile("tests/Datasets/Dataset.php")

        assertEquals(
            PestIcons.Dataset,
            getIcon(file.virtualFile)
        )
    }

    @Test
    fun testCanGetPestIconForDatasetFileWithTests() {
        myFixture.copyFileToProject("TestWithDataset.php", "/tests/Datasets/TestWithDataset.php")
        val file = myFixture.configureByFile("tests/Datasets/TestWithDataset.php")

        assertEquals(
            PestIcons.File,
            getIcon(file.virtualFile)
        )
    }

    @Test
    fun testCanGetPestIconForPestBaseFile() {
        val virtualFile = myFixture.copyFileToProject("Pest.php", "tests/Pest.php")

        val file = runReadActionBlocking { PsiManager.getInstance(myFixture.project).findFile(virtualFile)!! }

        assertEquals(
            PestIcons.Logo,
            getIcon(file.virtualFile)
        )
    }

    @Test
    fun testCanGetPestIconForPestFileWithPropertyCall() {
        val virtualFile = myFixture.copyFileToProject("SimpleHigherOrderNotTest.php", "tests/SimpleHigherOrderNotTest.php")

        val file = runReadActionBlocking { PsiManager.getInstance(myFixture.project).findFile(virtualFile)!! }

        assertEquals(
            PestIcons.File,
            getIcon(file.virtualFile)
        )
    }

    private fun getIcon(file: VirtualFile): Icon? = runReadActionBlocking {
        PestIconProvider().getIcon(file, ICON_FLAG_VISIBILITY, myFixture.project)
    }
}

package com.pestphp.pest

import com.intellij.openapi.util.Iconable.ICON_FLAG_VISIBILITY
import com.intellij.psi.PsiManager
import junit.framework.TestCase

class PestIconProviderTest : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("SimpleTest.php", "tests/SimpleTest.php")
    }

    override fun getTestDataPath(): String {
        return basePath
    }

    fun testCanGetPestIconForPestFile() {
        val file = myFixture.configureByFile("tests/SimpleTest.php")

        TestCase.assertEquals(
            PestIcons.File,
            PestIconProvider().getIcon(file.virtualFile, ICON_FLAG_VISIBILITY, project),
        )
    }

    fun testCanGetOtherIconForNonPestFile() {
        val file = myFixture.configureByFile("SimpleScript.php")

        assertNull(
            PestIconProvider().getIcon(file.virtualFile, ICON_FLAG_VISIBILITY, project)
        )
    }

    fun testCanGetPestIconForDatasetFile() {
        myFixture.copyFileToProject("Dataset.php", "/tests/Datasets/Dataset.php")
        val file = myFixture.configureByFile("tests/Datasets/Dataset.php")

        assertEquals(
            PestIcons.Dataset,
            PestIconProvider().getIcon(file.virtualFile, ICON_FLAG_VISIBILITY, project)
        )
    }

    fun testCanGetPestIconForDatasetFileWithTests() {
        myFixture.copyFileToProject("TestWithDataset.php", "/tests/Datasets/TestWithDataset.php")
        val file = myFixture.configureByFile("tests/Datasets/TestWithDataset.php")

        assertEquals(
            PestIcons.File,
            PestIconProvider().getIcon(file.virtualFile, ICON_FLAG_VISIBILITY, project)
        )
    }

    fun testCanGetPestIconForPestBaseFile() {
        val virtualFile = myFixture.copyFileToProject("Pest.php", "tests/Pest.php")

        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        assertEquals(
            PestIcons.Logo,
            PestIconProvider().getIcon(file.virtualFile, ICON_FLAG_VISIBILITY, project)
        )
    }

    fun testCanGetPestIconForPestFileWithPropertyCall() {
        val virtualFile = myFixture.copyFileToProject("SimpleHigherOrderNotTest.php", "tests/SimpleHigherOrderNotTest.php")

        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        assertEquals(
            PestIcons.File,
            PestIconProvider().getIcon(file.virtualFile, ICON_FLAG_VISIBILITY, project)
        )
    }
}

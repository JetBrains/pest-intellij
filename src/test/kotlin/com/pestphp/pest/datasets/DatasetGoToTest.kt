package com.pestphp.pest.datasets

import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/datasets")
class DatasetGoToTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/datasets"
    }

    fun testCanGoToDatasetInSameFile() {
        myFixture.configureByFile("DatasetReference.php")

        val declarationElement = GotoDeclarationAction.findTargetElement(
            project,
            myFixture.editor,
            myFixture.caretOffset
        ) as? FunctionReferenceImpl

        assertNotNull(declarationElement)
        assertTrue(declarationElement.isPestDataset())
        assertEquals("dojos", declarationElement!!.getPestDatasetName())
    }

    fun testCanGoToDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("SharedDatasetReference.php")

        val declarationElement = GotoDeclarationAction.findTargetElement(
            project,
            myFixture.editor,
            myFixture.caretOffset
        ) as? FunctionReferenceImpl

        assertNotNull(declarationElement)
        assertTrue(declarationElement.isPestDataset())
        assertEquals("stances", declarationElement!!.getPestDatasetName())
    }

    fun testCannotGoToDatasetInNonPestTest() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("DatasetOnNonPestTest.php")

        val declarationElement = GotoDeclarationAction.findTargetElement(
            project,
            myFixture.editor,
            myFixture.caretOffset
        )

        assertNull(declarationElement)
    }
}

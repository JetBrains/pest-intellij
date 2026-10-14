package com.pestphp.pest.features.datasets

import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/datasets")
class DatasetGoToTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/datasets")
    }

    @Test
    fun testCanGoToDatasetInSameFile() {
        myFixture.configureByFile("DatasetReference.php")

        val declarationElement = findTargetElement() as? FunctionReferenceImpl

        assertNotNull(declarationElement)
        assertTrue(runReadActionBlocking { declarationElement.isPestDataset() })
        assertEquals("dojos", runReadActionBlocking { declarationElement!!.getPestDatasetName() })
    }

    @Test
    fun testCanGoToDatasetInOtherFile() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("SharedDatasetReference.php")

        val declarationElement = findTargetElement() as? FunctionReferenceImpl

        assertNotNull(declarationElement)
        assertTrue(runReadActionBlocking { declarationElement.isPestDataset() })
        assertEquals("stances", runReadActionBlocking { declarationElement!!.getPestDatasetName() })
    }

    @Test
    fun testCannotGoToDatasetInNonPestTest() {
        myFixture.copyFileToProject("Datasets.php", "tests/Datasets/stances.php")
        myFixture.configureByFile("DatasetOnNonPestTest.php")

        val declarationElement = findTargetElement()

        assertNull(declarationElement)
    }

    @Test
    fun testCanGoToDatasetInDescribeBlock() {
        myFixture.configureByFile("DatasetInDescribeBlockReference.php")

        val declarationElement = findTargetElement() as? FunctionReferenceImpl

        assertNotNull(declarationElement)
        assertTrue(runReadActionBlocking { declarationElement.isPestDataset() })
        assertEquals("some_numbers", runReadActionBlocking { declarationElement!!.getPestDatasetName() })
    }

    private fun findTargetElement(): PsiElement? = runReadActionBlocking {
        GotoDeclarationAction.findTargetElement(
            myFixture.project,
            myFixture.editor,
            myFixture.caretOffset
        )
    }
}

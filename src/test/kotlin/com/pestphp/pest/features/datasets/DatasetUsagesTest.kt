package com.pestphp.pest.features.datasets

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.goto.PestDatasetUsagesGotoHandler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/goto/datasetUsages")
class DatasetUsagesTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/goto/datasetUsages")
    }

    @Test
    fun testGotoUsages() {
        myFixture.copyFileToProject("DatasetUsage.php", "tests/usages.php")
        myFixture.copyFileToProject("DatasetDeclaration.php", "tests/Datasets/declaration.php")
        val file = myFixture.configureFromTempProjectFile("tests/Datasets/declaration.php")

        val usages = runReadActionBlocking {
            val element = file.findElementAt(myFixture.caretOffset) ?: fail("No element")

            val targets = PestDatasetUsagesGotoHandler().getGotoDeclarationTargets(element, 0, null) ?: fail("No usages")
            targets.map { (it is MethodReference) to it.containingFile?.name }
        }

        assertEquals(2, usages.size)
        assertTrue(usages.any { (isMethodReference, fileName) -> isMethodReference && fileName == "usages.php" })
        assertTrue(usages.any { (isMethodReference, fileName) -> isMethodReference && fileName == "declaration.php" })
    }
}

package com.pestphp.pest.features.datasets

import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.goto.PestDatasetUsagesGotoHandler
import junit.framework.TestCase

@TestDataPath("/com/pestphp/pest/goto/datasetUsages")
class DatasetUsagesTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/goto/datasetUsages"
    }

    fun testGotoUsages() {
        myFixture.copyFileToProject("DatasetUsage.php", "tests/usages.php")
        myFixture.copyFileToProject("DatasetDeclaration.php", "tests/Datasets/declaration.php")
        val file = myFixture.configureFromTempProjectFile("tests/Datasets/declaration.php")

        val element = file.findElementAt(myFixture.caretOffset) ?: return fail("No element")

        val usages = PestDatasetUsagesGotoHandler().getGotoDeclarationTargets(element, 0, null) ?: return fail("No usages")

        assertEquals(2, usages.size)
        TestCase.assertTrue(usages.any { it is MethodReference && it.containingFile?.name == "usages.php" })
        TestCase.assertTrue(usages.any { it is MethodReference && it.containingFile?.name == "declaration.php" })
    }
}

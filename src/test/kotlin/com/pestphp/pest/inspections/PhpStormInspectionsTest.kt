package com.pestphp.pest.inspections

import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.inspections.psr0.PhpMultipleClassesDeclarationsInOneFile
import com.pestphp.pest.PestLightCodeFixture
import org.jetbrains.jps.model.java.JavaSourceRootType

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/inspections/phpstorm")
class PhpStormInspectionsTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/inspections/phpstorm"
    }

    private fun doTest(inspectionClass: Class<out PhpInspection>, testFilePath: String) {
        myFixture.enableInspections(inspectionClass)

        myFixture.configureByFile(testFilePath)

        myFixture.checkHighlighting()
    }

    fun testMultipleClassesDeclarationsInPestFile() {
        val testSrcDir = myFixture.tempDirFixture.findOrCreateDir("tests")
        PsiTestUtil.addSourceRoot(module, testSrcDir, JavaSourceRootType.TEST_SOURCE)
        myFixture.copyFileToProject("MultipleClassesDeclarationsInPestFileTest.php", "${testSrcDir.name}/MultipleClassesDeclarationsInPestFileTest.php")
        doTest(PhpMultipleClassesDeclarationsInOneFile::class.java, "${testSrcDir.name}/MultipleClassesDeclarationsInPestFileTest.php")
    }
}
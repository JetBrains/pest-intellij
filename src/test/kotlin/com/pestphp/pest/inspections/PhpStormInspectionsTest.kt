package com.pestphp.pest.inspections

import com.intellij.openapi.application.EDT
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.inspections.psr0.PhpMultipleClassesDeclarationsInOneFile
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/inspections/phpstorm")
class PhpStormInspectionsTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/inspections/phpstorm")
    }

    private fun doTest(inspectionClass: Class<out PhpInspection>, testFilePath: String) {
        myFixture.enableInspections(inspectionClass)

        myFixture.configureByFile(testFilePath)

        checkHighlighting()
    }

    @Test
    fun testMultipleClassesDeclarationsInPestFile() {
        val testSrcDir = myFixture.tempDirFixture.findOrCreateDir("tests")
        PsiTestUtil.addSourceRoot(myFixture.module, testSrcDir, JavaSourceRootType.TEST_SOURCE)
        myFixture.copyFileToProject("MultipleClassesDeclarationsInPestFileTest.php", "${testSrcDir.name}/MultipleClassesDeclarationsInPestFileTest.php")
        doTest(PhpMultipleClassesDeclarationsInOneFile::class.java, "${testSrcDir.name}/MultipleClassesDeclarationsInPestFileTest.php")
    }

    private fun checkHighlighting(): Unit = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            myFixture.checkHighlighting()
        }
    }
}

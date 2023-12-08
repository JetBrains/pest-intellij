package com.pestphp.pest.codeInsight.typeInference

import com.intellij.psi.PsiFile
import com.jetbrains.php.codeInsight.PhpTypeInferenceTestCase
import com.pestphp.pest.PestSettings
import com.pestphp.pest.PestTestUtils
import java.io.File

class PestTypeInferenceTest : PhpTypeInferenceTestCase() {
    override fun getTestDataHome(): String {
        return PestTestUtils.TEST_DATA_HOME
    }

    override fun getFixtureTestDataFolder(): String {
        return "codeInsight/typeInference"
    }

    override fun getFileBeforeRelativePath(fileBeforeExtension: String): String {
        return super.getFileBeforeRelativePath(fileBeforeExtension).replace('#', File.separatorChar)
    }

    private fun doTest(block: () -> PsiFile) {
        PestSettings.getInstance(project).pestFilePath = "Pest.php"
        val pestPhpFile = block()
        myFixture.openFileInEditor(pestPhpFile.getVirtualFile())
        doTypeTest()
    }

    fun testThisInInnerClosure() = doTest {
        addPhpFileToProject("Pest.php", "<?php")
    }

    fun `test$ThisInSubproject#Test`() = doTest {
        PestSettings.getInstance(project).pestFilePath = "ThisInSubproject/Pest.php"
        addPhpFileToProject(
            "ThisInSubproject/TestCase.php", """
            <?php
            
            abstract class TestCase { }
            """
        )
        addPhpFileToProject(
            "ThisInSubproject/Pest.php", """
            <?php
                        
            uses(TestCase::class)->in("./");
        """.trimIndent()
        )
    }
}
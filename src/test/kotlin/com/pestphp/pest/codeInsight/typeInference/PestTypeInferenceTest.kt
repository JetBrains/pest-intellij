package com.pestphp.pest.codeInsight.typeInference

import com.jetbrains.php.codeInsight.PhpTypeInferenceTestCase
import com.pestphp.pest.PestSettings
import com.pestphp.pest.PestTestUtils

class PestTypeInferenceTest : PhpTypeInferenceTestCase() {
    override fun getTestDataHome(): String {
        return PestTestUtils.TEST_DATA_HOME
    }

    override fun getFixtureTestDataFolder(): String {
        return "codeInsight/typeInference"
    }
    fun testThisInInnerClosure() {
        PestSettings.getInstance(project).pestFilePath = "Pest.php"
        val pestPhp = addPhpFileToProject("Pest.php", "<?php")
        myFixture.openFileInEditor(pestPhp.getVirtualFile())
        doTypeTest()
    }
}
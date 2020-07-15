package com.pestphp.pest.tests

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestFunction
import java.util.stream.Collectors

class PestUtilTest: PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("SimpleTest.php")
    }

    override fun getTestDataPath(): String? {
        return basePath + "fixtures"
    }

    fun testCanGetTestName() {
        val file = myFixture.configureByFile("SimpleTest.php")

        val functions = PsiTreeUtil.findChildrenOfType(file, FunctionReference::class.java)
                .stream().filter(FunctionReference::isPestTestFunction)
                .collect(Collectors.toList())

        assertEquals(1, functions.count())

        assertEquals(
                "basic",
                functions.first().getPestTestName()
        )
    }
}
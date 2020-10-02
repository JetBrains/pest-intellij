package com.pestphp.pest.tests.PestUtil

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestFunction
import com.pestphp.pest.tests.PestLightCodeFixture
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

        val functions = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
                .stream().filter(FunctionReferenceImpl::isPestTestFunction)
                .collect(Collectors.toList())

        assertEquals(1, functions.count())

        assertEquals(
                "it basic",
                functions.first().getPestTestName()
        )
    }

    fun testIsPestTestFunction() {

    }
}
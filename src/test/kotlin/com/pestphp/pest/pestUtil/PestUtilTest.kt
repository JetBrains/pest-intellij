package com.pestphp.pest.pestUtil

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestFunction
import java.util.stream.Collectors

class PestUtilTest : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("simpleTest.php")
    }

    override fun getTestDataPath(): String {
        return basePath
    }

    fun testCanGetTestName() {
        val file = myFixture.configureByFile("simpleTest.php")

        val functions = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
            .stream().filter(FunctionReferenceImpl::isPestTestFunction)
            .collect(Collectors.toList())

        assertEquals(1, functions.count())

        assertEquals(
            "it basic",
            functions.first().getPestTestName()
        )
    }
}

package com.pestphp.pest.utilTests

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestFunction
import java.util.stream.Collectors

class PestUtilTest : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("SimpleTest.php")
    }

    override fun getTestDataPath(): String {
        return "$basePath/utilTests"
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

    fun testClassNameResolutionTestName() {
        val file = myFixture.configureByFile("ClassNameResolutionTest.php")

        val functions = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
            .filter(FunctionReferenceImpl::isPestTestFunction)

        assertEquals(1, functions.count())

        assertEquals(
            "A",
            functions.first().getPestTestName()
        )
    }

    fun testClassNameResolutionInNamespaceTestName() {
        val file = myFixture.configureByFile("ClassNameResolutionInNamespaceTest.php")

        val functions = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
            .filter(FunctionReferenceImpl::isPestTestFunction)

        assertEquals(1, functions.count())

        assertEquals(
            "A\\\\B",
            functions.first().getPestTestName()
        )
    }
}

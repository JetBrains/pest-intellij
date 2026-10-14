package com.pestphp.pest.utilTests

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.stream.Collectors

@TestApplication
class PestUtilTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/utilTests")
        myFixture.copyFileToProject("SimpleTest.php")
    }

    @Test
    fun testCanGetTestName() {
        val file = myFixture.configureByFile("SimpleTest.php")

        val testNames = runReadActionBlocking {
            PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
                .stream().filter(FunctionReferenceImpl::isPestTestFunction)
                .collect(Collectors.toList())
                .map { it.getPestTestName() }
        }

        assertEquals(1, testNames.count())

        assertEquals(
            "it basic",
            testNames.first()
        )
    }

    @Test
    fun testClassNameResolutionTestName() {
        val file = myFixture.configureByFile("ClassNameResolutionTest.php")

        val testNames = runReadActionBlocking {
            PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
                .filter(FunctionReferenceImpl::isPestTestFunction)
                .map { it.getPestTestName() }
        }

        assertEquals(1, testNames.count())

        assertEquals(
            "A",
            testNames.first()
        )
    }

    @Test
    fun testClassNameResolutionInNamespaceTestName() {
        val file = myFixture.configureByFile("ClassNameResolutionInNamespaceTest.php")

        val testNames = runReadActionBlocking {
            PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
                .filter(FunctionReferenceImpl::isPestTestFunction)
                .map { it.getPestTestName() }
        }

        assertEquals(1, testNames.count())

        assertEquals(
            "A\\\\B",
            testNames.first()
        )
    }
}

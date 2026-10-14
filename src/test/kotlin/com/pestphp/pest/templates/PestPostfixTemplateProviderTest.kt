package com.pestphp.pest.templates

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

@TestApplication
class PestPostfixTemplateProviderTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private lateinit var testMethodName: String

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/templates")
        testMethodName = testInfo.testMethod.get().name
    }

    private fun doTest() {
        myFixture.configureByFile(PlatformTestUtil.getTestName(testMethodName, true) + ".php")
        myFixture.type('\t')
        myFixture.checkResultByFile(PlatformTestUtil.getTestName(testMethodName, true) + ".after.php", true)
    }

    @Test
    fun testIt() {
        doTest()
    }

    @Test
    fun testDescribe() {
        doTest()
    }
}
package com.pestphp.pest.surrounders

import com.intellij.codeInsight.generation.surroundWith.SurroundWithHandler
import com.intellij.lang.surroundWith.Surrounder
import com.intellij.openapi.application.EDT
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.BeforeEach

abstract class SurroundTestCase {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    /**
     * Returns the test data path of the test class.
     */
    protected open fun getTestDataPath(): String = PestTestUtil.getTestDataPath()

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, getTestDataPath())
    }

    protected fun doTest(surrounder: Surrounder, textBefore: String, textAfter: String) {
        myFixture.configureByText("simpleTest.php", textBefore)
        timeoutRunBlocking {
            withContext(Dispatchers.EDT) {
                SurroundWithHandler.invoke(myFixture.project, myFixture.editor, myFixture.file, surrounder)
            }
        }
        myFixture.checkResult(textAfter)
    }
}

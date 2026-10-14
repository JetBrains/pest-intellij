package com.pestphp.pest.types

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach

@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/types")
abstract class BaseTypeTestCase {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    protected val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/types")

        myFixture.copyFileToProject("TestCase.php")
    }
}

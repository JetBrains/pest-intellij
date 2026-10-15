package com.pestphp.pest.inspections

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/inspections")
class InvalidTestNameCaseInspectionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/inspections")
        myFixture.enableInspections(InvalidTestNameCaseInspection::class.java)
    }

    @Test
    fun testInvalidTestNameCase() {
        myFixture.configureByFile("InvalidTestNameCase.php")

        checkHighlighting()
    }

    @Test
    fun testValidTestNameCase() {
        myFixture.configureByFile("ValidTestNameCase.php")

        checkHighlighting()
    }

    @Test
    fun testReplacesInvalidTestNameCase() {
        myFixture.configureByFile("InvalidTestNameCase.php")

        checkHighlighting { it }.forEach { myFixture.launchAction(it) }

        myFixture.checkResultByFile("InvalidTestNameCase.after.php")
    }

    @Test
    fun testValidTestNameWhenWrongCasingOnSomeWords() {
        myFixture.configureByFile("ValidTestNameWhenWrongCasingOnOneWord.php")

        checkHighlighting()
    }

    @Test
    fun testValidTestNameCaseInHigherOrderTest() {
        myFixture.configureByFile("ValidHigherOrderTestNameCase.php")

        checkHighlighting()
    }

    @Test
    fun testValidItTestNameWithoutSpaces() {
        myFixture.configureByFile("ValidItTestNameWithoutSpaces.php")

        checkHighlighting()
    }

    @Test
    fun testInvalidTestNameAndDatasetName() {
        myFixture.configureByFile("InvalidTestNameAndDatasetName.php")

        checkHighlighting { it }.forEach { myFixture.launchAction(it) }

        myFixture.checkResultByFile("InvalidTestNameAndDatasetName.after.php")
    }

    private fun checkHighlighting() {
        myFixture.checkHighlighting()
    }

    private fun <T> checkHighlighting(pickQuickFix: (List<IntentionAction>) -> T): T {
        myFixture.checkHighlighting()
        return pickQuickFix(myFixture.getAllQuickFixes())
    }
}

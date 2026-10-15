package com.pestphp.pest.features.datasets

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/datasets")
class InvalidDatasetNameCaseInspectionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/datasets")
        myFixture.enableInspections(InvalidDatasetNameCaseInspection::class.java)
    }

    @Test
    fun testInvalidDatasetNameCase() {
        myFixture.configureByFile("InvalidDatasetNameCase.php")

        checkHighlighting { it }.forEach { myFixture.launchAction(it) }

        myFixture.checkResultByFile("InvalidDatasetNameCase.after.php")
    }

    @Test
    fun testValidDatasetNameCase() {
        myFixture.configureByFile("ValidDatasetNameCase.php")

        checkHighlighting()
    }

    private fun checkHighlighting() {
        myFixture.checkHighlighting()
    }

    private fun <T> checkHighlighting(pickQuickFix: (List<IntentionAction>) -> T): T {
        myFixture.checkHighlighting()
        return pickQuickFix(myFixture.getAllQuickFixes())
    }
}

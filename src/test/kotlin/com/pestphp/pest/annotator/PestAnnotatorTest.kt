package com.pestphp.pest.annotator

import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestLightCodeFixture

class PestAnnotatorTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/annotator"
    }

    override fun setUp() {
        super.setUp()
        myFixture.copyDirectoryToProject("stub", ".")
    }

    fun testHasDuplicateCustomExpectation() {
        myFixture.configureByFile("DuplicateCustomExpectation.php")

        myFixture.checkHighlighting()
    }

    fun testDeleteDuplicateCustomExpectation() {
        myFixture.configureByFile("DuplicateCustomExpectation.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().first { it.familyName == PestBundle.message("QUICK_FIX_DELETE_CUSTOM_EXPECTATION", "toBeOne") }
            .run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateCustomExpectation.afterDelete.php")
    }

    fun testNavigateToNextDuplicateCustomExpectation() {
        myFixture.configureByFile("DuplicateCustomExpectation.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().last { it.familyName == PestBundle.message("INTENTION_NAVIGATE_TO_DUPLICATE_CUSTOM_EXPECTATION") }
            .run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateCustomExpectation.afterNavigate.php")
    }

    fun testNoDuplicateCustomExpectation() {
        myFixture.configureByFile("NoDuplicateCustomExpectation.php")

        myFixture.checkHighlighting()
    }

    fun testHasDuplicateTest() {
        myFixture.configureByFile("DuplicateTestName.php")

        myFixture.checkHighlighting()
    }

    fun testDeleteDuplicateTest() {
        myFixture.configureByFile("DuplicateTestName.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().first { it.familyName == PestBundle.message("QUICK_FIX_DELETE_TEST", "basic") }
            .run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateTestName.afterDelete.php")
    }

    fun testNavigateToDuplicateTest() {
        myFixture.configureByFile("DuplicateTestName.php")

        myFixture.checkHighlighting()
        myFixture.getAllQuickFixes().last { it.familyName == PestBundle.message("INTENTION_NAVIGATE_TO_DUPLICATE_TEST_NAME") }
            .run { myFixture.launchAction(this) }

        myFixture.checkResultByFile("DuplicateTestName.afterNavigate.php")
    }

    fun testNoDuplicateTest() {
        myFixture.configureByFile("NoDuplicateTestName.php")

        myFixture.checkHighlighting()
    }
}
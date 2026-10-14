package com.pestphp.pest.inspections

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.application.EDT
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PestAssertionCanBeSimplifiedInspectionTest {
  companion object {
    private val projectFixture = projectFixture(openAfterCreation = true)
  }

  private val myFixture by codeInsightModuleFixture(projectFixture)

  @BeforeEach
  fun setUp() {
    PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/inspections/assertionCanBeSimplified")
    myFixture.enableInspections(PestAssertionCanBeSimplifiedInspection::class.java)
  }

  @Test
  fun testToBeWithTrue() {
    myFixture.configureByFile("ToBeWithTrue.php")

    checkHighlighting { it }.forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToBeWithTrue.after.php")
  }

  @Test
  fun testToBeWithFalse() {
    myFixture.configureByFile("ToBeWithFalse.php")

    checkHighlighting { it }.forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToBeWithFalse.after.php")
  }

  @Test
  fun testToBeWithNull() {
    myFixture.configureByFile("ToBeWithNull.php")

    checkHighlighting { it }.forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToBeWithNull.after.php")
  }

  @Test
  fun testToHaveCountWithZero() {
    myFixture.configureByFile("ToHaveCountWithZero.php")

    checkHighlighting { it }.forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToHaveCountWithZero.after.php")
  }

  private fun <T> checkHighlighting(pickQuickFix: (List<IntentionAction>) -> T): T = timeoutRunBlocking {
    withContext(Dispatchers.EDT) {
      myFixture.checkHighlighting()
      pickQuickFix(myFixture.getAllQuickFixes())
    }
  }
}

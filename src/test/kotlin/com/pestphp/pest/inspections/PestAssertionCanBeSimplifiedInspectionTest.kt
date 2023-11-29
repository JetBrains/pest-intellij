package com.pestphp.pest.inspections

import com.pestphp.pest.PestLightCodeFixture

class PestAssertionCanBeSimplifiedInspectionTest: PestLightCodeFixture() {
  override fun getTestDataPath(): String {
    return "src/test/resources/com/pestphp/pest/inspections/assertionCanBeSimplified"
  }

  override fun setUp() {
    super.setUp()

    myFixture.enableInspections(PestAssertionCanBeSimplifiedInspection::class.java)
  }

  fun testToBeWithTrue() {
    myFixture.configureByFile("ToBeWithTrue.php")

    myFixture.checkHighlighting()
    myFixture.getAllQuickFixes().forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToBeWithTrue.after.php")
  }

  fun testToBeWithFalse() {
    myFixture.configureByFile("ToBeWithFalse.php")

    myFixture.checkHighlighting()
    myFixture.getAllQuickFixes().forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToBeWithFalse.after.php")
  }

  fun testToBeWithNull() {
    myFixture.configureByFile("ToBeWithNull.php")

    myFixture.checkHighlighting()
    myFixture.getAllQuickFixes().forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToBeWithNull.after.php")
  }

  fun testToHaveCountWithZero() {
    myFixture.configureByFile("ToHaveCountWithZero.php")

    myFixture.checkHighlighting()
    myFixture.getAllQuickFixes().forEach { myFixture.launchAction(it) }

    myFixture.checkResultByFile("ToHaveCountWithZero.after.php")
  }
}
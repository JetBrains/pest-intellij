package com.pestphp.pest.inspections

import com.pestphp.pest.PestLightCodeFixture

class DuplicateExtendNameInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/inspections"
    }

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(DuplicateCustomExpectationInspection::class.java)
    }

    fun testHasDuplicateCustomExpectation() {
        myFixture.configureByFile("DuplicateCustomExpectation.php")

        myFixture.checkHighlighting()
    }

    fun testNoDuplicateTest() {
        myFixture.configureByFile("NoDuplicateCustomExpectation.php")

        myFixture.checkHighlighting()
    }
}

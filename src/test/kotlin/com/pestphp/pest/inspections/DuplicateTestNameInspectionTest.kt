package com.pestphp.pest.inspections

import com.pestphp.pest.PestLightCodeFixture

class DuplicateTestNameInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/inspections"
    }

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(DuplicateTestNameInspection::class.java)
    }

    fun testHasDuplicateTest() {
        myFixture.configureByFile("DuplicateTestName.php")

        myFixture.checkHighlighting()
    }

    fun testNoDuplicateTest() {
        myFixture.configureByFile("NoDuplicateTestName.php")

        myFixture.checkHighlighting()
    }
}

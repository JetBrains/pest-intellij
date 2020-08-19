package com.pestphp.pest.inspections

import com.pestphp.pest.tests.PestLightCodeFixture


class DuplicateTestNameInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
        return "src/test/resources/com/pestphp/pest/inspections"
    }

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(DuplicateTestNameInspection::class.java)
    }

    fun testHasDuplicateTest() {
        val file = myFixture.configureByFile("DuplicateTestName.php")

        myFixture.checkHighlighting()
    }

    fun testNoDuplicateTest() {
        val file = myFixture.configureByFile("NoDuplicateTestName.php")

        myFixture.checkHighlighting()
    }
}

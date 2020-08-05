package com.pestphp.pest.tests.configuration

import com.pestphp.pest.tests.PestLightCodeFixture

class PestConfigurationFileTest: PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject("tests", "tests")
    }

    override fun getTestDataPath(): String? {
        return basePath + "configuration/fixtures"
    }

    fun testUnit() {
        myFixture.configureByFile("tests/Unit/UnitTest.php")

        assertCompletion("baseTestFunc")
    }

    fun testUsesUnit() {
        myFixture.configureByFile("tests/Unit/UsesUnitTest.php")

        assertCompletion("baseTestFunc", "traitFunc")
    }

    fun testFeature() {
        myFixture.configureByFile("tests/Feature/FeatureTest.php")

        assertCompletion("baseTestFunc", "featureTestFunc")
    }

    fun testGroupedFeature() {
        myFixture.configureByFile("tests/GroupedFeature/GroupedFeatureTest.php")

        assertCompletion("baseTestFunc", "featureTestFunc", "someBaseTraitFunc")
    }
}
package com.pestphp.pest.configuration.pest

import com.pestphp.pest.PestLightCodeFixture

class PestConfigurationFileTest : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject(".", "tests")
    }

    override fun getTestDataPath(): String {
        return "$basePath/configuration/pest"
    }

    fun testUnit() {
        myFixture.configureByFile("tests/Unit/UnitTest.php")

        assertCompletion("baseTestFunc")
    }

    fun testPestExtendUsesUnit() {
        myFixture.configureByFile("tests/Unit/PestExtendUnitTest.php")

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

    fun testDIRFeature() {
        myFixture.configureByFile("tests/DIRFeature/FeatureTest.php")

        assertCompletion("baseTestFunc", "featureTestFunc")
    }

    fun testDynamicFolderFeature() {
        myFixture.configureByFile("tests/DynamicFeature/FeatureTest.php")

        assertCompletion("baseTestFunc", "featureTestFunc")
    }
}

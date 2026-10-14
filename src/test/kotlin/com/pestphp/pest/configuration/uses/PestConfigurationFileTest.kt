package com.pestphp.pest.configuration.uses

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class PestConfigurationFileTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/configuration/uses")
        myFixture.copyDirectoryToProject(".", "tests")
    }

    @Test
    fun testUnit() {
        myFixture.configureByFile("tests/Unit/UnitTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc")
    }

    @Test
    fun testUsesUnit() {
        myFixture.configureByFile("tests/Unit/UsesUnitTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "traitFunc")
    }

    @Test
    fun testFeature() {
        myFixture.configureByFile("tests/Feature/FeatureTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "featureTestFunc")
    }

    @Test
    fun testGroupedFeature() {
        myFixture.configureByFile("tests/GroupedFeature/GroupedFeatureTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "featureTestFunc", "someBaseTraitFunc")
    }

    @Test
    fun testDIRFeature() {
        myFixture.configureByFile("tests/DIRFeature/FeatureTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "featureTestFunc")
    }

    @Test
    fun testDynamicFolderFeature() {
        myFixture.configureByFile("tests/DynamicFeature/FeatureTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "featureTestFunc")
    }

    @Test
    fun testGlobPatternDirectory() {
        myFixture.configureByFile("tests/GlobPattern/DirectoryTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "featureTestFunc")
    }

    @Test
    fun testGlobPatternFile() {
        myFixture.configureByFile("tests/GlobPattern/FileTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "featureTestFunc")
    }

    @Test
    fun testGlobPatternFileWithRelativePath() {
        myFixture.configureByFile("tests/GlobPattern/FileWithRelativePathTest.php")

        PestTestUtil.assertCompletion(myFixture, "baseTestFunc", "featureTestFunc")
    }
}

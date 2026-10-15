package com.pestphp.pest.features.datasets

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/datasets")
class InvalidDatasetReferenceInspectionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/datasets")
        myFixture.enableInspections(InvalidDatasetReferenceInspection::class.java)
    }

    @Test
    fun testHasInvalidDatasetName() {
        myFixture.configureByFile("InvalidDatasetTest.php")

        checkHighlighting()
    }

    @Test
    fun testHasValidDataset() {
        myFixture.configureByFile("DatasetTest.php")

        checkHighlighting()
    }

    @Test
    fun testHasWithStatementWithNoArgs() {
        myFixture.configureByFile("DatasetNoArgsTest.php")

        checkHighlighting()
    }

    @Test
    fun testHasInvalidDatasetInDescribeBlock() {
        myFixture.configureByFile("InvalidDatasetInDescribeBlockTest.php")

        checkHighlighting()
    }

    @Test
    fun testDatasetInsideDescribeBlock() {
        myFixture.configureByFile("DatasetInsideDescribeBlockTest.php")

        checkHighlighting()
    }

    private fun checkHighlighting() {
        myFixture.checkHighlighting()
    }
}

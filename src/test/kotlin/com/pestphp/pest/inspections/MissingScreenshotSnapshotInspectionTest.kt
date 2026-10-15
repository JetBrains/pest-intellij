package com.pestphp.pest.inspections

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/inspections")
class MissingScreenshotSnapshotInspectionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/inspections")
        myFixture.enableInspections(MissingScreenshotSnapshotInspection::class.java)
    }

    @Test
    fun testSimple() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/MissingScreenshotSnapshot.php")
        checkHighlighting()
    }

    @Test
    fun testSimpleExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/ScreenshotSnapshot.php")
        checkHighlighting()
    }

    @Test
    fun testMultiple() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/MissingScreenshotSnapshotMultiple.php")
        checkHighlighting()
    }

    @Test
    fun testMultipleExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/ScreenshotSnapshotMultiple.php")
        checkHighlighting()
    }

    @Test
    fun testComplexName() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/MissingScreenshotSnapshotComplexName.php")
        checkHighlighting()
    }

    @Test
    fun testComplexNameExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/ScreenshotSnapshotComplexName.php")
        checkHighlighting()
    }

    @Test
    fun testNestedDir() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/nested/MissingScreenshotSnapshotNested.php")
        checkHighlighting()
    }

    @Test
    fun testNestedDirExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/nested/ScreenshotSnapshotNested.php")
        checkHighlighting()
    }

    private fun checkHighlighting() {
        myFixture.checkHighlighting()
    }
}

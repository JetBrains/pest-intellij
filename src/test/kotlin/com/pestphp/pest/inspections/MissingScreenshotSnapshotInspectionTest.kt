package com.pestphp.pest.inspections

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath($$"$CONTENT_ROOT/resources/com/pestphp/pest/inspections")
class MissingScreenshotSnapshotInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/inspections"
    }

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(MissingScreenshotSnapshotInspection::class.java)
    }

    fun testSimple() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/MissingScreenshotSnapshot.php")
        myFixture.checkHighlighting()
    }

    fun testSimpleExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/ScreenshotSnapshot.php")
        myFixture.checkHighlighting()
    }

    fun testMultiple() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/MissingScreenshotSnapshotMultiple.php")
        myFixture.checkHighlighting()
    }

    fun testMultipleExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/ScreenshotSnapshotMultiple.php")
        myFixture.checkHighlighting()
    }

    fun testComplexName() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/MissingScreenshotSnapshotComplexName.php")
        myFixture.checkHighlighting()
    }

    fun testComplexNameExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/ScreenshotSnapshotComplexName.php")
        myFixture.checkHighlighting()
    }

    fun testNestedDir() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/nested/MissingScreenshotSnapshotNested.php")
        myFixture.checkHighlighting()
    }

    fun testNestedDirExists() {
        myFixture.copyDirectoryToProject("screenshotProject", ".")
        myFixture.configureFromTempProjectFile("tests/Feature/nested/ScreenshotSnapshotNested.php")
        myFixture.checkHighlighting()
    }
}

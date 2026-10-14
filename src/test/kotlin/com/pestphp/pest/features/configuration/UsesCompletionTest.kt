package com.pestphp.pest.features.configuration

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/configuration/uses")
class UsesCompletionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/configuration/uses")
    }

    @Test
    fun testCanCompleteSubFolder() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "other/Test.php")
        myFixture.configureByFile("CompleteInFolder.php")

        PestTestUtil.assertCompletion(myFixture, "myFolder", "other")
    }

    @Test
    fun testDoesNotCompleteInNotUsesInStatement() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "other/Test.php")
        myFixture.configureByFile("CompleteFakeInFolder.php")

        PestTestUtil.assertNoCompletion(myFixture)
    }

    @Test
    fun testOnlyCompletesFoldersUnderFile() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "tests/Feature/Test.php")
        myFixture.copyFileToProject("Test.php", "tests/Unit/Test.php")
        val virtualFile = myFixture.copyFileToProject("CompleteInFolder.php", "tests/Pest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        PestTestUtil.assertAllCompletion(myFixture, "Feature", "Unit")
    }
}

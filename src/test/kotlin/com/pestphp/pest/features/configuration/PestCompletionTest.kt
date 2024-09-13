package com.pestphp.pest.features.configuration

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/features/configuration/pest")
class PestCompletionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/features/configuration/pest"
    }

    fun testCanCompleteSubFolder() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "other/Test.php")
        myFixture.configureByFile("CompleteInFolder.php")

        assertCompletion("myFolder", "other")
    }

    fun testDoesNotCompleteInNotPestInStatement() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "other/Test.php")
        myFixture.configureByFile("CompleteFakePestInFolder.php")

        assertNoCompletion()
    }

    fun testOnlyCompletesFoldersUnderFile() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "tests/Feature/Test.php")
        myFixture.copyFileToProject("Test.php", "tests/Unit/Test.php")
        val virtualFile = myFixture.copyFileToProject("CompleteInFolder.php", "tests/Pest.php")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        assertAllCompletion("Feature", "Unit")
    }
}
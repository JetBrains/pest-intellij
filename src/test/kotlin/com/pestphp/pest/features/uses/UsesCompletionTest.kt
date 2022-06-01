package com.pestphp.pest.features.uses

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/features/uses")
class UsesCompletionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/features/uses"
    }

    fun testCanCompleteSubFolder() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "other/Test.php")
        myFixture.configureByFile("CompleteInFolder.php")

        assertCompletion("myFolder", "other")
    }

    fun testDoesNotCompleteInNotUsesInStatement() {
        myFixture.copyFileToProject("Test.php", "myFolder/Test.php")
        myFixture.copyFileToProject("Test.php", "other/Test.php")
        myFixture.configureByFile("CompleteFakeInFolder.php")

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

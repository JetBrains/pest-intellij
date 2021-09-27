package com.pestphp.pest.customExpectations

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.customExpects
import com.pestphp.pest.customExpectations.generators.Method
import com.pestphp.pest.toMethod

class CustomExpectationIndexTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/customExpectations"
    }

    fun testCustomExpectationIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("CustomExpectation.php")
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = fileBasedIndex.getValues(
            CustomExpectationIndex.key,
            "/src/CustomExpectation.php",
            GlobalSearchScope.projectScope(project)
        ).flatten()

        assertSize(1, values)
        assertEquals(
            values,
            file.customExpects.map { it.toMethod() }
        )
    }

    fun testCustomExpectationReturningUserType() {
        val virtualFile = myFixture.copyFileToProject("CustomUserExpectation.php")
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = fileBasedIndex.getValues(
            CustomExpectationIndex.key,
            "/src/CustomUserExpectation.php",
            GlobalSearchScope.projectScope(project)
        ).flatten()

        assertSize(1, values)
        assertEquals(
            values,
            file.customExpects.map { it.toMethod() }
        )
    }

    fun testCustomExpectationWithParameter() {
        val virtualFile = myFixture.copyFileToProject("CustomExpectationWithParameter.php")
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = fileBasedIndex.getValues(
            CustomExpectationIndex.key,
            "/src/CustomExpectationWithParameter.php",
            GlobalSearchScope.projectScope(project)
        ).flatten()

        assertSize(1, values)
        assertEquals(
            values,
            file.customExpects.map { it.toMethod() }
        )
    }

    fun testCustomThisExpectation() {
        val virtualFile = myFixture.copyFileToProject("CustomThisExpectation.php")
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = fileBasedIndex.getValues(
            CustomExpectationIndex.key,
            "/src/CustomThisExpectation.php",
            GlobalSearchScope.projectScope(project)
        ).flatten()

        assertSize(1, values)
        assertEquals(
            values,
            file.customExpects.map { it.toMethod() }
        )
    }

    fun testCustomExpectationEventIsPublished() {
        var changedCount = 0;

        project.messageBus.connect().subscribe(
            CustomExpectationNotifier.TOPIC,
            object: CustomExpectationNotifier {
                override fun changedExpectation(file: PsiFile, customExpectations: List<Method>) {
                    if (file.name == "CustomExpectation.php") {
                        changedCount++
                    }
                }
            }
        )

        myFixture.copyFileToProject("CustomExpectation.php")

        val fileBasedIndex = FileBasedIndex.getInstance()
        fileBasedIndex.getAllKeys(CustomExpectationIndex.key, project)

        assertEquals(1, changedCount)
    }

    fun testCustomExpectationIsIndexedTwoSameNamedFile() {
        myFixture.copyFileToProject("CustomExpectation.php")
        myFixture.copyFileToProject("subFolder/CustomExpectation.php")

        val fileBasedIndex = FileBasedIndex.getInstance()
        val keys = fileBasedIndex.getAllKeys(
            CustomExpectationIndex.key,
            project
        )

        assertContainsElements(
            keys,
            "/src/CustomExpectation.php"
        )
        assertContainsElements(
            keys,
            "/src/subFolder/CustomExpectation.php"
        )
    }
}
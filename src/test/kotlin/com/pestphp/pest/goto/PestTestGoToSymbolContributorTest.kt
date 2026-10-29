package com.pestphp.pest.goto

import com.intellij.navigation.NavigationItem
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.IndexingTestUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/goto/PestTestGoToSymbolContributor")
class PestTestGoToSymbolContributorTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/goto/PestTestGoToSymbolContributor")
        myFixture.copyFileToProject("FileWithPestTests.php", "tests/FileWithPestTests.php")
        IndexingTestUtil.waitUntilIndexesAreReady(project)
    }

    @Test
    fun testNamesContainPestTestNames() {
        val names = runReadActionBlocking { PestTestGoToSymbolContributor().getNames(project, false) }

        assertTrue(names.toList().containsAll(listOf("basic", "it does something")), "${names.toList()}")
    }

    @Test
    fun testItemsByNameFindPestTest() {
        val presentableTexts = runReadActionBlocking {
            itemsByName("it does something").map { it.presentation?.presentableText }
        }

        assertEquals(listOf("it does something"), presentableTexts)
    }

    @Test
    fun testItemsByNameIsEmptyForUnknownName() {
        val items = runReadActionBlocking { itemsByName("unknown") }

        assertEquals(emptyList<NavigationItem>(), items)
    }

    @Test
    fun testItemCanNavigateToSource() {
        val canNavigateToSource = runReadActionBlocking { itemsByName("basic").single().canNavigateToSource() }

        assertTrue(canNavigateToSource)
    }

    private fun itemsByName(name: String): List<NavigationItem> =
        PestTestGoToSymbolContributor().getItemsByName(name, name, project, false).toList()
}

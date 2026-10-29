package com.pestphp.pest.customExpectations.symbols

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.impl.DefaultUsageSearchParameters
import com.intellij.find.usages.impl.searchTargets
import com.intellij.model.psi.PsiSymbolReferenceService
import com.intellij.model.search.SearchService
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.features.customExpectations.symbols.PestCustomExpectationReference
import com.pestphp.pest.features.customExpectations.symbols.PestCustomExpectationSymbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/customExpectations/symbols")
class PestCustomExpectationSymbolTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/customExpectations/symbols")
    }

    @Test
    fun testReferenceResolvesToDeclaredExpectation() {
        myFixture.configureByFiles("Usage.php", "Expectations.php")

        val (name, parameterNames) = runReadActionBlocking {
            val symbol = resolveAtCaret().single()
            symbol.expectationName to symbol.methodDescriptor.parameters.map { it.name }
        }

        assertEquals("toBeOne", name)
        assertEquals(listOf("value", "label"), parameterNames)
    }

    @Test
    fun testSymbolRangeCoversExpectationName() {
        myFixture.configureByFiles("Usage.php", "Expectations.php")

        val nameInFile = runReadActionBlocking {
            val symbol = resolveAtCaret().single()
            symbol.rangeInFile.substring(symbol.file.text)
        }

        assertEquals("toBeOne", nameInFile)
    }

    @Test
    fun testSymbolPointerRestoresSymbol() {
        myFixture.configureByFiles("Usage.php", "Expectations.php")

        val (symbol, restored) = runReadActionBlocking {
            val symbol = resolveAtCaret().single()
            symbol to symbol.createPointer().dereference()
        }

        assertEquals(symbol, restored)
        assertEquals(symbol.rangeInFile, restored?.rangeInFile)
    }

    @Test
    fun testFindUsagesFindsDeclarationAndEveryCall() {
        myFixture.configureByFiles("Usage.php", "Expectations.php")

        val usages = runReadActionBlocking {
            val project = myFixture.project
            val target = searchTargets(myFixture.file, myFixture.caretOffset).single()
            SearchService.getInstance()
                .searchParameters(DefaultUsageSearchParameters(project, target, GlobalSearchScope.projectScope(project)))
                .findAll()
                .filterIsInstance<PsiUsage>()
                .map { "${it.file.name}:${it.range.substring(it.file.text)}:${it.declaration}" }
                .sorted()
        }

        assertEquals(listOf("Expectations.php:toBeOne:true", "Usage.php:toBeOne:false", "Usage.php:toBeOne:false"), usages)
    }

    @Test
    fun testRenameUpdatesDeclarationAndCalls() {
        myFixture.configureByFiles("Usage.php", "Expectations.php")
        val target = runReadActionBlocking { resolveAtCaret().single() }

        myFixture.renameTarget(target, "toBeTwo")

        myFixture.checkResultByFile("Usage.php", "Usage.after.php", false)
        myFixture.checkResultByFile("Expectations.php", "Expectations.after.php", false)
    }

    private fun resolveAtCaret(): List<PestCustomExpectationSymbol> {
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        val methodReference = PsiTreeUtil.getParentOfType(element, MethodReference::class.java)!!
        return PsiSymbolReferenceService.getService().getReferences(methodReference)
            .filterIsInstance<PestCustomExpectationReference>()
            .flatMap { it.resolveReference() }
            .filterIsInstance<PestCustomExpectationSymbol>()
    }
}

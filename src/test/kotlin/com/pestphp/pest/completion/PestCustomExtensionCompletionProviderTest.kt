package com.pestphp.pest.completion

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.codeInsight.lookup.LookupElementRenderer
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/customExpectations/symbols")
class PestCustomExtensionCompletionProviderTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/customExpectations/symbols")
    }

    @Test
    fun testLookupPresentsResolvedParameterTypes() {
        myFixture.configureByFiles("Completion.php", "Expectations.php")
        val lookupElement = myFixture.completeBasic().single { it.lookupString == "toBeOne" }

        @Suppress("UNCHECKED_CAST")
        val renderer = lookupElement.expensiveRenderer as LookupElementRenderer<LookupElement>

        val presentation = runReadActionBlocking {
            LookupElementPresentation().also { renderer.renderElement(lookupElement, it) }
        }

        assertEquals("(value: int, label: string)", presentation.tailText)
        assertEquals("bool", presentation.typeText)
    }
}

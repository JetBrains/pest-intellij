package com.pestphp.pest.customExpectations

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.testFramework.utils.parameterInfo.MockCreateParameterInfoContext
import com.intellij.testFramework.utils.parameterInfo.MockParameterInfoUIContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.features.customExpectations.CustomExpectationParameterInfoHandler
import com.pestphp.pest.features.customExpectations.generators.Parameter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/customExpectations/symbols")
class CustomExpectationParameterInfoHandlerTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val handler = CustomExpectationParameterInfoHandler()

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/customExpectations/symbols")
    }

    @Test
    fun testParameterInfoShowsCustomExpectationParameters() {
        myFixture.configureByFiles("ParameterInfo.php", "Expectations.php")

        val parameterNames = runReadActionBlocking { findParameters()?.map { it.name } }

        assertEquals(listOf("value", "label"), parameterNames)
    }

    @Test
    fun testParameterInfoPresentsResolvedParameterTypes() {
        myFixture.configureByFiles("ParameterInfo.php", "Expectations.php")

        val text = runReadActionBlocking {
            val context = MockCreateParameterInfoContext(myFixture.editor, myFixture.file)
            val element = handler.findElementForParameterInfo(context)!!
            @Suppress("UNCHECKED_CAST")
            val parameters = context.itemsToShow!!.single() as List<Parameter>
            val uiContext = MockParameterInfoUIContext<FunctionReference>(element)
            handler.updateUI(parameters, uiContext)
            uiContext.text
        }

        assertEquals("value: int, label: string", text)
    }

    @Test
    fun testNoParameterInfoForBuiltInExpectation() {
        myFixture.configureByFiles("BuiltInParameterInfo.php", "Expectations.php")

        val parameters = runReadActionBlocking { findParameters() }

        assertNull(parameters)
    }

    private fun findParameters(): List<Parameter>? {
        val context = MockCreateParameterInfoContext(myFixture.editor, myFixture.file)
        handler.findElementForParameterInfo(context) ?: return null
        @Suppress("UNCHECKED_CAST")
        return context.itemsToShow?.single() as List<Parameter>?
    }
}

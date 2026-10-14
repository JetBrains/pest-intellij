package com.pestphp.pest.customExpectations.generators

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.features.customExpectations.generators.ExpectationGenerator
import com.pestphp.pest.features.customExpectations.generators.Method
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@TestApplication
class ExpectationGeneratorTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
    }

    @Test
    fun testCanGenerateFileWithMethod() {
        val generator = ExpectationGenerator()
        generator.docMethods.add(
            Method(
                "works",
                PhpType.BOOLEAN,
                emptyList()
            )
        )

        val result = generator.generate(myFixture.project)

        val expectedClass = ExpectationGeneratorTest::class.java
            .getResource("/com/pestphp/pest/customExpectations/generators/ExpectationGenerator/GeneratedWithMethod.php")
            ?.readText() ?: fail("File not found.")
        assertEquals(
            result,
            expectedClass
        )
    }
}

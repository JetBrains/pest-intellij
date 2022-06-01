package com.pestphp.pest.customExpectations.generators

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.features.customExpectations.generators.ExpectationGenerator
import com.pestphp.pest.features.customExpectations.generators.Method

class ExpectationGeneratorTest : PestLightCodeFixture() {
    fun testCanGenerateFileWithMethod() {
        val generator = ExpectationGenerator()
        generator.docMethods.add(
            Method(
                "works",
                PhpType.BOOLEAN,
                emptyList()
            )
        )

        val result = generator.generate(project)

        val expectedClass = ExpectationGeneratorTest::class.java
            .getResource("/com/pestphp/pest/customExpectations/generators/ExpectationGenerator/GeneratedWithMethod.php")
            ?.readText() ?: fail("File not found.")
        assertEquals(
            result,
            expectedClass
        )
    }
}

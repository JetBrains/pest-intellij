package com.pestphp.pest.generators

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestLightCodeFixture
import junit.framework.TestCase

class ExpectationGeneratorTest : PestLightCodeFixture() {
    fun testCanGenerateFileWithMethod() {
        val generator = ExpectationGenerator()
        generator.docMethods.add(
            ExpectationGenerator.Method(
                "works",
                PhpType.BOOLEAN,
                emptyList()
            )
        )

        val result = generator.generate(project)

        val expectedClass = ExpectationGeneratorTest::class.java
            .getResource("/com/pestphp/pest/generators/ExpectationGenerator/GeneratedWithMethod.php")
            ?.readText() ?: fail("File not found.")
        assertEquals(
            result,
            expectedClass
        )
    }
}

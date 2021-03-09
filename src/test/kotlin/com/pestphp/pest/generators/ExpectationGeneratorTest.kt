package com.pestphp.pest.generators

import com.jetbrains.php.lang.psi.resolve.types.PhpType
import junit.framework.TestCase

class ExpectationGeneratorTest : TestCase() {
    fun testCanGenerateFileWithMethod() {
        val generator = ExpectationGenerator()
        generator.docMethods.add(
            ExpectationGenerator.Method(
                "works",
                PhpType.BOOLEAN,
                emptyList()
            )
        )

        val result = generator.generate()

        val expectedClass = ExpectationGeneratorTest::class.java
            .getResource("/com/pestphp/pest/generators/ExpectationGenerator/GeneratedWithMethod.php")
            ?.readText() ?: fail("File not found.")
        assertEquals(
            result,
            expectedClass
        )
    }
}

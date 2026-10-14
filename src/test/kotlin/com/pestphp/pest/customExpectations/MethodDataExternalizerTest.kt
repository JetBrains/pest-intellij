package com.pestphp.pest.customExpectations

import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.util.io.DataOutputStream
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.features.customExpectations.externalizers.MethodDataExternalizer
import com.pestphp.pest.features.customExpectations.generators.Method
import com.pestphp.pest.features.customExpectations.generators.Parameter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream

@TestApplication
class MethodDataExternalizerTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
    }

    private fun saveAndRead(method: Method): Method {
        val dataOutput = ByteArrayOutputStream()

        val externalizer = MethodDataExternalizer()
        externalizer.save(
            DataOutputStream(dataOutput),
            method
        )

        val dataInput = DataInputStream(
            ByteArrayInputStream(dataOutput.toByteArray())
        )

        return externalizer.read(dataInput)
    }

    @Test
    fun testCanExternalizeMethod() {
        val method = Method(
            "works",
            PhpType.BOOLEAN,
            emptyList()
        )

       val deserializedMethod = saveAndRead(method)

        assertEquals(
            method,
            deserializedMethod
        )
    }

    @Test
    fun testCanExternalizeMethodWithMultipleTypes() {
        val method = Method(
            "works",
            PhpType.builder().add("random").add(PhpType.EXCEPTION).build(),
            emptyList()
        )

        val deserializedMethod = saveAndRead(method)

        assertEquals(
            method,
            deserializedMethod
        )
    }

    @Test
    fun testCanExternalizeMethodWithParameter() {
        val method = Method(
            "getName",
            PhpType.BOOLEAN,
            listOf(
                Parameter(
                    "name",
                    PhpType.STRING,
                )
            )
        )

        val deserializedMethod = saveAndRead(method)

        assertEquals(
            method,
            deserializedMethod
        )
    }

    @Test
    fun testCanExternalizeMethodWithParameterWithDefaultValue() {
        val method = Method(
            "getName",
            PhpType.BOOLEAN,
            listOf(
                Parameter(
                    "name",
                    PhpType.STRING,
                    "'Oliver'"
                )
            )
        )

        val deserializedMethod = saveAndRead(method)

        assertEquals(
            method,
            deserializedMethod
        )
    }

    @Test
    fun testCanExternalizeMethodWithMultipleParameters() {
        val method = Method(
            "someMethod",
            PhpType.BOOLEAN,
            listOf(
                Parameter(
                    "parameter1",
                    PhpType.STRING,
                ),
                Parameter(
                    "otherParameter",
                    PhpType.STRING,
                    "'someValue'"
                )
            )
        )

        val deserializedMethod = saveAndRead(method)

        assertEquals(
            method,
            deserializedMethod
        )
    }
}
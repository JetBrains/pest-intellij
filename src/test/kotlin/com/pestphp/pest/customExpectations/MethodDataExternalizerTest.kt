package com.pestphp.pest.customExpectations

import com.intellij.util.io.DataOutputStream
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.features.customExpectations.externalizers.MethodDataExternalizer
import com.pestphp.pest.features.customExpectations.generators.Method
import com.pestphp.pest.features.customExpectations.generators.Parameter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream

class MethodDataExternalizerTest : PestLightCodeFixture() {
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
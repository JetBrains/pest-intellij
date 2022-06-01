package com.pestphp.pest.customExpectations

import com.intellij.util.io.DataOutputStream
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.features.customExpectations.externalizers.ListDataExternalizer
import com.pestphp.pest.features.customExpectations.externalizers.MethodDataExternalizer
import com.pestphp.pest.features.customExpectations.generators.Method
import com.pestphp.pest.features.customExpectations.generators.Parameter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream

class ListMethodDataExternalizerTest : PestLightCodeFixture() {
    private fun saveAndRead(method: List<Method>): List<Method> {
        val dataOutput = ByteArrayOutputStream()

        val externalizer = ListDataExternalizer(MethodDataExternalizer.INSTANCE)
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
        val methods = listOf(
            Method(
                "works",
                PhpType.BOOLEAN,
                emptyList()
            )
        )

       val deserializedMethods = saveAndRead(methods)

        assertEquals(
            methods,
            deserializedMethods
        )
    }

    fun testCanExternalizeMultipleMethods() {
        val methods = listOf(
            Method(
                "works",
                PhpType.BOOLEAN,
                emptyList()
            ),
            Method(
                "alsoWorks",
                PhpType.BOOLEAN,
                emptyList()
            )
        )

        val deserializedMethods = saveAndRead(methods)

        assertEquals(
            methods,
            deserializedMethods
        )
    }

    fun testCanExternalizeMethodsWithMultipleTypes() {
        val methods = listOf(
            Method(
                "works",
                PhpType.builder().add("random").add(PhpType.EXCEPTION).build(),
                emptyList()
            ),
            Method(
                "valid",
                PhpType.builder().add("some").add(PhpType.STRING).build(),
                emptyList()
            )
        )

        val deserializedMethods = saveAndRead(methods)

        assertEquals(
            methods,
            deserializedMethods
        )
    }

    fun testCanExternalizeMethodsWithParameter() {
        val methods = listOf(
            Method(
                "getName",
                PhpType.BOOLEAN,
                listOf(
                    Parameter(
                        "name",
                        PhpType.STRING,
                    )
                )
            ),
            Method(
                "somethingElse",
                PhpType.BOOLEAN,
                listOf()
            ),
            Method(
                "alsoWorks",
                PhpType.BOOLEAN,
                listOf(
                    Parameter(
                        "value",
                        PhpType.BOOLEAN,
                    )
                )
            ),
        )

        val deserializedMethods = saveAndRead(methods)

        assertEquals(
            methods,
            deserializedMethods
        )
    }

    fun testCanExternalizeMethodWithParameterWithDefaultValue() {
        val methods = listOf(
            Method(
                "getName",
                PhpType.BOOLEAN,
                listOf(
                    Parameter(
                        "name",
                        PhpType.STRING,
                        "'Oliver'"
                    )
                )
            ),
            Method(
                "otherMethod",
                PhpType.BOOLEAN,
                listOf(
                    Parameter(
                        "name",
                        PhpType.STRING,
                        "true"
                    )
                )
            )
        )

        val deserializedMethods = saveAndRead(methods)

        assertEquals(
            methods,
            deserializedMethods
        )
    }

    fun testCanExternalizeMethodWithMultipleParameters() {
        val methods = listOf(
            Method(
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
            ),
            Method(
                "andAnotherOne",
                PhpType.EXCEPTION,
                listOf(
                    Parameter(
                        "parameter3",
                        PhpType.STRING,
                    ),
                    Parameter(
                        "otherParameter2",
                        PhpType.STRING,
                        "false"
                    )
                )
            )
        )

        val deserializedMethods = saveAndRead(methods)

        assertEquals(
            methods,
            deserializedMethods
        )
    }
}
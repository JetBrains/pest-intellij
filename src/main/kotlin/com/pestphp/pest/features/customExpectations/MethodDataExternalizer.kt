package com.pestphp.pest.features.customExpectations

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.stubs.indexes.StringSetDataExternalizer
import com.pestphp.pest.features.customExpectations.generators.Method
import com.pestphp.pest.features.customExpectations.generators.Parameter
import java.io.DataInput
import java.io.DataOutput

class MethodDataExternalizer : DataExternalizer<Method> {
    override fun save(out: DataOutput, value: Method) {
        EnumeratorStringDescriptor.INSTANCE.save(out, value.name)

        var returnType = value.returnType.toString()
        if (!value.returnType.isComplete) {
            returnType = returnType.removeSuffix("|?")
        }

        EnumeratorStringDescriptor.INSTANCE.save(
            out,
            returnType
        )
        StringSetDataExternalizer.INSTANCE.save(
            out,
            value.parameters
                .map { it.toString() }
                .toSet()
        )
    }

    override fun read(input: DataInput): Method {
        val name = EnumeratorStringDescriptor.INSTANCE.read(input)
        val returnType = EnumeratorStringDescriptor.INSTANCE.read(input)
        val parameterString = StringSetDataExternalizer.INSTANCE.read(input)

        val parameters = parameterString.reversed().map {
            Parameter(
                name = Regex("name='(.*?)'")
                    .find(it)!!
                    .groupValues[1],
                returnType = PhpType.builder()
                    .add(
                        Regex("returnType='(.*?)'")
                            .find(it)!!
                            .groupValues[1]
                    ).build(),
                defaultValue = Regex("defaultValue='(.*)'")
                    .find(it)!!
                    .groupValues[1]
                    .ifEmpty { null }
            )
        }

        return Method(
            name,
            PhpType().add(returnType),
            parameters
        )
    }
}
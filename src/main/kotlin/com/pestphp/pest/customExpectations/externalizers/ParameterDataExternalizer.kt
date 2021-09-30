package com.pestphp.pest.customExpectations.externalizers

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.NullableDataExternalizer
import com.pestphp.pest.customExpectations.generators.Parameter
import java.io.DataInput
import java.io.DataOutput

class ParameterDataExternalizer : DataExternalizer<Parameter> {
    companion object {
        val INSTANCE = ParameterDataExternalizer()
    }

    override fun save(out: DataOutput, value: Parameter) {
        EnumeratorStringDescriptor.INSTANCE.save(out, value.name)
        PhpTypeDataExternalizer.INSTANCE.save(out, value.returnType)
        NullableDataExternalizer(EnumeratorStringDescriptor.INSTANCE).save(
            out,
            value.defaultValue
        )
    }

    override fun read(input: DataInput): Parameter {
        val name = EnumeratorStringDescriptor.INSTANCE.read(input)
        val returnType = PhpTypeDataExternalizer.INSTANCE.read(input)
        val defaultValue = NullableDataExternalizer(EnumeratorStringDescriptor.INSTANCE).read(input)

        return Parameter(
            name,
            returnType,
            defaultValue
        )
    }
}
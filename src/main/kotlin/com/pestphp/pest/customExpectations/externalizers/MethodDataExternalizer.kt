package com.pestphp.pest.customExpectations.externalizers

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.pestphp.pest.customExpectations.generators.Method
import java.io.DataInput
import java.io.DataOutput

class MethodDataExternalizer : DataExternalizer<Method> {
    companion object {
        val INSTANCE = MethodDataExternalizer()
    }

    override fun save(out: DataOutput, value: Method) {
        EnumeratorStringDescriptor.INSTANCE.save(out, value.name)
        PhpTypeDataExternalizer.INSTANCE.save(out, value.returnType)
        ListDataExternalizer(ParameterDataExternalizer.INSTANCE).save(
            out,
            value.parameters
        )
    }

    override fun read(input: DataInput): Method {
        val name = EnumeratorStringDescriptor.INSTANCE.read(input)
        val returnType = PhpTypeDataExternalizer.INSTANCE.read(input)
        val parameters = ListDataExternalizer(ParameterDataExternalizer.INSTANCE).read(input)

        return Method(
            name,
            returnType,
            parameters
        )
    }
}
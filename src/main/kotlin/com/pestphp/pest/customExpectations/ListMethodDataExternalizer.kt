package com.pestphp.pest.customExpectations

import com.intellij.util.io.DataExternalizer
import com.pestphp.pest.customExpectations.generators.Method
import java.io.DataInput
import java.io.DataOutput

class ListMethodDataExternalizer : DataExternalizer<List<Method>> {
    override fun save(out: DataOutput, value: List<Method>) {
        out.writeInt(value.size)

        val methodExternalizer = MethodDataExternalizer()
        value.forEach {
            methodExternalizer.save(out, it)
        }
    }

    override fun read(input: DataInput): List<Method> {
        val size = input.readInt()
        val methodExternalizer = MethodDataExternalizer()
        val methods = mutableListOf<Method>()

        repeat(size) {
            methods.add(
                methodExternalizer.read(input)
            )
        }

        return methods
    }
}
package com.pestphp.pest.features.customExpectations.externalizers

import com.intellij.util.io.DataExternalizer
import java.io.DataInput
import java.io.DataOutput

class ListDataExternalizer<K>(private val dataExternalizer: DataExternalizer<K>) : DataExternalizer<List<K>> {
    override fun save(out: DataOutput, value: List<K>) {
        out.writeInt(value.size)

        value.forEach {
            dataExternalizer.save(out, it)
        }
    }

    override fun read(input: DataInput): List<K> {
        val size = input.readInt()
        val list = mutableListOf<K>()

        repeat(size) {
            list.add(
                dataExternalizer.read(input)
            )
        }

        return list
    }
}
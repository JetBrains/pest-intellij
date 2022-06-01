package com.pestphp.pest.features.customExpectations.externalizers

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import java.io.DataInput
import java.io.DataOutput

class PhpTypeDataExternalizer : DataExternalizer<PhpType> {
    companion object {
        val INSTANCE = PhpTypeDataExternalizer()
    }

    override fun save(out: DataOutput, value: PhpType) {
        var endValue = value.toString()

        if (!value.isComplete) {
            endValue = endValue.removeSuffix("|?")
        }

        EnumeratorStringDescriptor.INSTANCE.save(
            out,
            endValue
        )
    }

    override fun read(input: DataInput): PhpType {
        val type = EnumeratorStringDescriptor.INSTANCE.read(input)

        return PhpType().add(type)
    }
}
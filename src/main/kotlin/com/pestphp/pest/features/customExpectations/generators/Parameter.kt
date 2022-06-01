package com.pestphp.pest.features.customExpectations.generators

import com.jetbrains.php.lang.psi.resolve.types.PhpType

class Parameter(val name: String, val returnType: PhpType, val defaultValue: String? = null) {
    override fun toString(): String {
        val defaultValue = defaultValue ?: ""

        return "Parameter(name='$name', returnType='$returnType', defaultValue='$defaultValue')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Parameter

        if (name != other.name) return false
        if (returnType.toString() != other.returnType.toString()) return false
        if (defaultValue != other.defaultValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + returnType.toString().hashCode()
        result = 31 * result + (defaultValue?.hashCode() ?: 0)
        return result
    }
}
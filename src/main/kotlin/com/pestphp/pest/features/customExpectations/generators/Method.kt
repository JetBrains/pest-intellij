package com.pestphp.pest.features.customExpectations.generators

import com.jetbrains.php.lang.psi.resolve.types.PhpType

class Method(val name: String, val returnType: PhpType, val parameters: List<Parameter>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Method

        if (name != other.name) return false
        if (returnType.toString() != other.returnType.toString()) return false


        if (parameters != other.parameters) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + returnType.toString().hashCode()
        result = 31 * result + parameters.hashCode()
        return result
    }

    override fun toString(): String {
        return "Method(name='$name', returnType=$returnType, parameters=$parameters)"
    }

}
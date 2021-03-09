package com.pestphp.pest.generators

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.resolve.types.PhpType

/**
 * Generates A class for expectations.
 */
class ExpectationGenerator {
    val docMethods: MutableList<Method> = mutableListOf()

    fun generate(): String {
        return docMethods
            .joinToString("\n") { " * @method ${it.returnType.toStringResolved()} ${it.name}(${it.parametersAsString().joinToString(separator = ", ")})" }
            .let { //language=InjectablePHP
                """
                |<?php
                |
                |namespace Pest\Expectations;
                |
                |/**
                |$it
                | */
                |final class Expectation {}
                """.trimMargin()
            }
    }

    fun generateToFile(project: Project): PsiFile {
        return PsiFileFactory.getInstance(project).createFileFromText(
            "Expectation." + PhpFileType.INSTANCE.defaultExtension,
            PhpFileType.INSTANCE,
            generate()
        )
    }

    class Method(val name: String, val returnType: PhpType, val parameters: List<Parameter>) {
        fun parametersAsString(): List<String> {
            return parameters.map {
                var parameterAsString = "${it.globalType.toStringResolved()} $${it.name}"

                if (it.defaultValuePresentation !== null) {
                    parameterAsString += " = ${it.defaultValuePresentation}"
                }

                parameterAsString
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Method

            if (name != other.name) return false
            if (returnType != other.returnType) return false
            if (parameters != other.parameters) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + returnType.hashCode()
            result = 31 * result + parameters.hashCode()
            return result
        }
    }
}
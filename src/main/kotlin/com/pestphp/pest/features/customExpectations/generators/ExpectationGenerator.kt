package com.pestphp.pest.features.customExpectations.generators

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.jetbrains.php.lang.PhpFileType

/**
 * Generates A class for expectations.
 */
class ExpectationGenerator {
    val docMethods: MutableList<Method> = mutableListOf()

    fun generate(project: Project): String {
        return docMethods
            .joinToString("\n") { methodString(it, project) }
            .let { //language=InjectablePHP
                """
                |<?php
                |
                |namespace Pest;
                |
                |/**
                |$it
                | */
                |final class Expectation {}
                """.trimMargin()
            }
    }

    private fun methodString(method: Method, project: Project): String {
        val returnType = method.returnType.global(project).toStringResolved()
        val parameters = method.parametersAsString(project).joinToString(separator = ", ")

        return " * @method $returnType ${method.name}($parameters)"
    }

    fun generateToFile(project: Project): PsiFile {
        return PsiFileFactory.getInstance(project).createFileFromText(
            "Expectation." + PhpFileType.INSTANCE.defaultExtension,
            PhpFileType.INSTANCE,
            generate(project)
        )
    }

}

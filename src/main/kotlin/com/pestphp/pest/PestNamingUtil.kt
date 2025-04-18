package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import com.intellij.psi.util.parents
import com.intellij.remote.RemoteSdkProperties
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.run.remote.PhpRemoteInterpreterManager
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.runner.getLocationUrl
import java.util.*

fun FunctionReferenceImpl.getPestTestName(): String? {
    val testName = getParameter(0)?.stringValue ?: return tryGetArchTestName(this)

    val parent = this.findParentOfType<FunctionReferenceImpl>()
    val prepend = if (parent is FunctionReferenceImpl && parent.isDescribeFunction()) {
        parent.getPestTestName()
    } else {
        ""
    }

    return when (this.canonicalText) {
        "it" -> "${prepend}it $testName"
        "describe" -> "${prepend}`$testName` → "
        else -> "${prepend}$testName"
    }
}

private fun tryGetArchTestName(functionReference: FunctionReference): String? =
    if (functionReference.canonicalText == "arch") {
        getArchTestName(functionReference)
    } else {
        null
    }

private fun getArchTestName(functionReference: FunctionReference): String {
    val parents = functionReference.parents(false).takeWhile { it !is Statement }.toList()
    return parents.joinToString(separator = " → ") { element ->
        val name = if (element is PhpReference) element.canonicalText else element.text
        val parameters = if (element is ParameterListOwner) getParametersString(element) else ""
        "$name$parameters"
    }
}

private fun getParametersString(element: ParameterListOwner) =
    " " + when (val elem = element.parameters.firstOrNull()) {
        is ArrayCreationExpression -> elem.children.filterIsInstance<PhpPsiElement>().joinToString(prefix = "[", postfix = "]") { it.text }
        is StringLiteralExpression -> elem.text
        else -> ""
    }.replace("\"", "'")

val PsiElement.stringValue: String?
    get() = when (this) {
        is StringLiteralExpression -> this.contents
        is ConcatenationExpression -> this.contents
        is ClassConstantReference -> {
            val classRef = this.classReference
            if (classRef is ClassReference && this.isStatic && this.lastChild.text == "class") {
                classRef.fqn
                    ?.removePrefix("\\")
                    ?.replace("\\", "\\\\")
            } else null
        }
        else -> null
    }

val ConcatenationExpression.contents: String?
    get() {
        val left = this.leftOperand?.stringValue
        val right = this.rightOperand?.stringValue

        if (left === null || right === null) {
            return null
        }

        return left + right
    }

fun PsiElement?.getPestTestName(): String? {
    return when (this) {
        is MethodReference -> (this.classReference as? FunctionReference)?.getPestTestName()
        is FunctionReferenceImpl -> this.getPestTestName()
        else -> null
    }
}

fun PsiElement?.getInitialFunctionReference(): FunctionReference? {
    return when (this) {
        is MethodReference -> (this.classReference as? FunctionReference).getInitialFunctionReference()
        is FunctionReferenceImpl -> this
        else -> null
    }
}

fun PsiElement.toPestTestRegex(workingDirectory: String): String? {
    return this.getPestTestName()?.toPestTestRegex(
        workingDirectory,
        this.containingFile.virtualFile.path,
        PhpPathMapper.create(this.project)
    )
}

fun PsiElement.toPestFqn(): List<String> {
    val testName = this.getPestTestName() ?: return emptyList()

    val file = this.containingFile.virtualFile.path

    return PhpInterpretersManagerImpl.getInstance(this.project)
        .interpreters
        .asSequence()
        .map { it.phpSdkAdditionalData }
        .filter { it is RemoteSdkProperties }
        .mapNotNull {
            PhpRemoteInterpreterManager.getInstance()?.createPathMappings(
                this.project,
                it
            )
        }
        .map { it.convertToRemote(file) }
        .map { "pest_qn://$it::$testName" }
        .plus("${getLocationUrl(this.containingFile)}::$testName")
        .toList()
}

fun String.toPestTestRegex(rootPath: String, file: String, pathMapper: PhpPathMapper): String {
    val mappedWorkingDirectory = pathMapper.getRemoteFilePath(rootPath) ?: rootPath
    val mappedFile = pathMapper.getRemoteFilePath(file) ?: file

    // Follow the steps for class name generation
    // 1. Take the path of the PEST file from the cwd.
    val fqn = mappedFile.withoutFirstFileSeparator
        .removePrefix(mappedWorkingDirectory.withoutFirstFileSeparator)
        .withoutFirstFileSeparator
        // 2. Make the first folder's first letter uppercase.
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        // 3. Remove file extension.
        .substringBeforeLast('.')
        // 4. Make directory separators to namespace separators.
        .replace("\\", "\\\\")
        .replace("/", "\\\\")
        // 5. Remove unsupported characters
        .replace("-", "")
        .replace("_", "")
        // 6. Add P as a namespace before the generated namespace.
        .let { "(P\\\\)?$it" }

    // Allow substring matching only for "describe" block execution
    val possibleEndOfLine = if (this.endsWith(" → ")) "" else "$"

    // Escape characters
    val testName = this
        .replace(" ", "\\s")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("[", "\\[")
        .replace("]", "\\]")
        .replace("^", "\\^")
        .replace("/", "\\/")
        .replace("?", "\\?")
        .replace("+", "\\+")

    // Match the description of a single data set
    val dataSet = """(data\sset\s".*"|\(.*\))"""

    return """^$fqn::$testName(\swith\s$dataSet(\s\/\s$dataSet)*(\s#\d+)?)?$possibleEndOfLine"""
}

val String.withoutFirstFileSeparator: String
    get() {
        return this.removePrefix("/").removePrefix("\\")
    }

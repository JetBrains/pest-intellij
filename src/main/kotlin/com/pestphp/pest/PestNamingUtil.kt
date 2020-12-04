package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.util.pathmapper.PhpPathMapper

fun FunctionReferenceImpl.getPestTestName(): String? {
    val testName = (getParameter(0) as? StringLiteralExpression)?.contents

    if (this.canonicalText == "it") {
        return "it $testName"
    }
    return testName
}

fun PsiElement?.getPestTestName(): String? {
    return when (this) {
        is MethodReference -> (this.classReference as? FunctionReference)?.getPestTestName()
        is FunctionReferenceImpl -> this.getPestTestName()
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

fun String.toPestTestRegex(workingDirectory: String, file: String, pathMapper: PhpPathMapper): String {
    val mappedWorkingDirectory = pathMapper.getRemoteFilePath(workingDirectory) ?: workingDirectory
    val mappedFile = pathMapper.getRemoteFilePath(file) ?: file

    // Follow the steps for class name generation TODO: add link
    // 1. Take the path of the PEST file from the cwd.
    val fqn = mappedFile.withoutFirstFileSeparator
        .removePrefix(mappedWorkingDirectory.withoutFirstFileSeparator)
        .withoutFirstFileSeparator
        // 2. Make the first folder's first letter uppercase.
        .capitalize()
        // 3. Remove file extension.
        .substringBeforeLast('.')
        // 4. Make directory separators to namespace separators.
        .replace("\\", "\\\\")
        .replace("/", "\\\\")
        // 5. Add P as a namespace before the generated namespace.
        .let { "P\\\\$it" }

    val testName = this.replace(" ", "\\s")

    return "^$fqn::$testName(\\swith\\s\\(.*\\)(\\s#\\d+)?)?\$"
}

val String.withoutFirstFileSeparator: String
    get() {
        return this.removePrefix("/").removePrefix("\\")
    }

package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import java.io.File

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
    return this.getPestTestName()?.toPestTestRegex(workingDirectory, this.containingFile.virtualFile.path)
}

fun String.toPestTestRegex(workingDirectory: String, file: String): String {
    // Follow the steps for class name generation TODO: add link
    // 1. Take the path of the PEST file from the cwd.
    val fqn = file.removePrefix(File.separator)
        .removePrefix(workingDirectory.removePrefix(File.separator))
        .removePrefix(File.separator)
        // 2. Make the first folder's first letter uppercase.
        .capitalize()
        // 3. Remove file extension.
        .substringBeforeLast('.')
        // 4. Make directory separators to namespace separators.
        .replace(File.separator, "\\\\")
        // 5. Add P as a namespace before the generated namespace.
        .let { "P\\\\$it" }

    val testName = this.replace(" ", "\\s")

    return "^$fqn::$testName(\\swith\\s\\(.*\\)(\\s#\\d+)?)?\$"
}

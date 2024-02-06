package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType

val PEST_TEST_CALL_TYPE = PhpType.from(
    "\\Pest\\PendingCalls\\TestCall", // for Pest versions >= 2.x
    "\\Pest\\PendingObjects\\TestCall" // for Pest versions 1.x
)

fun PsiElement?.isPestTestReference(isSmart: Boolean = false): Boolean {
    return when (this) {
        null -> false
        is MethodReference -> this.isPestTestMethodReference(isSmart)
        is FunctionReferenceImpl -> this.isPestTestFunction(isSmart)
        else -> false
    }
}

private val testNames = setOf("it", "test", "todo", "describe", "arch")
fun FunctionReferenceImpl.isPestTestFunction(isSmart: Boolean = false): Boolean {
    if (this.canonicalText !in testNames) return false
    return !isSmart ||
        this.resolveGlobal(false).any {
            val type = (it as? Function)?.type
            if (type == null) return@any false
            PEST_TEST_CALL_TYPE.isConvertibleFromGlobal(project, type)
        }
}

fun FunctionReferenceImpl.isPestBeforeFunction(): Boolean {
    return this.canonicalText == "beforeEach"
}

fun FunctionReferenceImpl.isPestAfterFunction(): Boolean {
    return this.canonicalText == "afterEach"
}

private val allPestNames = setOf("it", "test", "todo", "beforeEach", "afterEach", "dataset", "describe", "arch")
fun FunctionReferenceImpl.isAnyPestFunction(): Boolean {
    return this.canonicalText in allPestNames
}

fun FunctionReferenceImpl.isDescribeFunction(): Boolean {
    return this.canonicalText == "describe"
}


fun MethodReference.isPestTestMethodReference(isSmart: Boolean = false): Boolean {
    return when (val reference = classReference) {
        is FunctionReferenceImpl -> reference.isPestTestFunction(isSmart)
        is MethodReference -> reference.isPestTestMethodReference(isSmart)
        is FieldReference -> reference.isPestTestMethodReference(isSmart)
        else -> false
    }
}

fun FieldReference.isPestTestMethodReference(isSmart: Boolean = false): Boolean {
    return when (val reference = classReference) {
        is FunctionReferenceImpl -> reference.isPestTestFunction(isSmart)
        is MethodReference -> reference.isPestTestMethodReference(isSmart)
        is FieldReference -> reference.isPestTestMethodReference(isSmart)
        else -> false
    }
}

fun PsiFile.getRoot(): List<PsiElement> {
    val element = this.firstChild

    return element.children.filterIsInstance<PhpNamespace>()
        .mapNotNull { it.statements }
        .getOrElse(
            0
        ) { element }
        .children
        .filterIsInstance<Statement>()
        .mapNotNull { it.firstChild }
}

fun PsiFile.getPestTests(isSmart: Boolean = false): Set<FunctionReference> {
    if (this !is PhpFile) return setOf()

    return this.getRoot()
        .filter { it.isPestTestReference(isSmart) }
        .filterIsInstance<FunctionReference>()
        .toSet()
}

package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
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
    return !isSmart || (this.resolveLocal().isEmpty() && PhpIndex.getInstance(project).getFunctionsByName(this.canonicalText).any { function ->
        PEST_TEST_CALL_TYPE.isConvertibleFromGlobal(project, function.type)
    })
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

/**
 * Traverses elements and recursively enters describe blocks, collecting items via the collector function.
 */
internal fun <T> collectFromDescribeBlocks(
    elements: List<PhpPsiElement>,
    collector: (PhpPsiElement) -> T?
): List<T> {
    val result = mutableListOf<T>()

    for (element in elements) {
        collector(element)?.let { result.add(it) }

        val funcRef = element as? FunctionReferenceImpl
        if (funcRef != null && funcRef.isDescribeFunction()) {
            val closure = (funcRef.parameters.getOrNull(1) as? PhpExpression)?.firstChild as? Function
            val body = closure?.children?.filterIsInstance<GroupStatement>()?.firstOrNull()
            val statements = body?.statements?.mapNotNull { it.firstChild }?.filterIsInstance<PhpPsiElement>() ?: emptyList()
            result.addAll(collectFromDescribeBlocks(statements, collector))
        }
    }

    return result
}

fun PsiFile.getPestTests(isSmart: Boolean = false): Set<FunctionReference> {
    return collectFromDescribeBlocks(this.getRootPhpPsiElements()) { element ->
        if (element.isPestTestReference(isSmart)) element as? FunctionReference else null
    }.toSet()
}

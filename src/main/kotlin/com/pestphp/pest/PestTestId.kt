package com.pestphp.pest

import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl

private const val SEPARATOR = " → "

/**
 * Structured identity of a Pest `it` / `test` / `describe` / `arch` call: the chain of enclosing
 * describe blocks plus the call's own description, in Pest's own wording.
 *
 * Two projections are derived from that chain and MUST NOT be confused:
 *  - [filterId]        the machine identifier matched against Pest's `--filter` regex and its
 *                      `--log-teamcity` output. Carries backticked describe names and a trailing
 *                      " → " sentinel for describe (prefix) runs. NEVER shown to a user.
 *  - [presentableName] the human label: no backticks, no trailing sentinel, interior " → " kept.
 *
 * Everywhere the PSI is available, build a [PestTestId] and read the projection you need. The only
 * places that still project from a raw string are the genuine string boundaries — the persisted
 * run-configuration `methodName` and the test-runner proxy names — which go through
 * [presentableOf].
 */
internal data class PestTestId(val segments: List<Segment>, val describePrefix: Boolean) {
    /** One nesting level: [filterText] is Pest's wire form, [displayText] the human form. */
    data class Segment(val displayText: String, val filterText: String)

    /** Machine identifier. Kept byte-for-byte compatible with the legacy `getPestTestName()`. */
    val filterId: String
        get() = segments.joinToString(SEPARATOR) { it.filterText } + if (describePrefix) SEPARATOR else ""

    val presentableName: String
        get() = segments.joinToString(SEPARATOR) { it.displayText }

    /** Innermost segment only — the structure-view node label. */
    val localName: String
        get() = segments.lastOrNull()?.displayText.orEmpty()

    companion object {
        /**
         * Projection used ONLY at string boundaries where the PSI is gone and all we hold is a
         * persisted [filterId] (run-config methodName) or a runner proxy name.
         */
        fun presentableOf(filterId: String): String =
            filterId.removeSuffix(SEPARATOR).replace("`", "")
    }
}

internal fun PsiElement?.pestTestId(): PestTestId? = when (this) {
    is MethodReference -> (classReference as? FunctionReference).pestTestId()
    is FunctionReferenceImpl -> this.pestTestId()
    else -> null
}

private fun FunctionReferenceImpl.pestTestId(): PestTestId? {
    val testName = getParameter(0)?.stringValue
        ?: return archTestName()?.let { PestTestId(listOf(PestTestId.Segment(it, it)), describePrefix = false) }

    // Consecutive describe ancestors, outermost first (stops at the first non-describe parent).
    val describeAncestors = generateSequence(findParentOfType<FunctionReferenceImpl>()) {
        it.findParentOfType<FunctionReferenceImpl>()
    }.takeWhile { it.isDescribeFunction() }.toList().asReversed()

    val segments = describeAncestors.mapTo(mutableListOf()) { describe ->
        val name = describe.getParameter(0)?.stringValue.orEmpty()
        PestTestId.Segment(displayText = name, filterText = "`$name`")
    }
    segments += when (canonicalText) {
        "it" -> PestTestId.Segment("it $testName", "it $testName")
        "describe" -> PestTestId.Segment(testName, "`$testName`")
        else -> PestTestId.Segment(testName, testName)
    }
    return PestTestId(segments, describePrefix = isDescribeFunction())
}

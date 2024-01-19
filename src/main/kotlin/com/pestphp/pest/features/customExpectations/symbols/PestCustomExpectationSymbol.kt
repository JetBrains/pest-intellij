@file:Suppress("UnstableApiUsage")

package com.pestphp.pest.features.customExpectations.symbols

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageHandler
import com.intellij.model.Pointer
import com.intellij.model.Symbol
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.refactoring.rename.api.RenameTarget
import com.pestphp.pest.features.customExpectations.generators.Method

class PestCustomExpectationSymbol(
    @NlsSafe val expectationName: String,
    val file: PsiFile,
    val rangeInFile: TextRange,
    val methodDescriptor: Method
) : Symbol, NavigationTarget, SearchTarget, RenameTarget {

    override fun createPointer() = Pointer.fileRangePointer(
        file,
        TextRange(rangeInFile.startOffset - 1, rangeInFile.endOffset + 1)
    ) { restoredFile, restoredRange ->
        // pointer doesn't survive when element is of zero range
        PestCustomExpectationSymbol(
            expectationName,
            restoredFile,
            TextRange(restoredRange.startOffset + 1, restoredRange.endOffset - 1),
            methodDescriptor
        )
    }

    override val maximalSearchScope: SearchScope
        get() = GlobalSearchScope.projectScope(file.project)

    override val targetName = expectationName

    override fun presentation() = computePresentation()

    override val usageHandler: UsageHandler
        get() = UsageHandler.createEmptyUsageHandler(expectationName)

    override fun equals(other: Any?) =
        (other as? PestCustomExpectationSymbol)?.let { expectationName == it.expectationName } ?: false

    override fun hashCode() = expectationName.hashCode()

    override fun computePresentation() = TargetPresentation.builder(expectationName).presentation()

    override fun navigationRequest() = NavigationRequest.sourceNavigationRequest(file, rangeInFile)
}

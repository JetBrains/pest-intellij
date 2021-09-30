package com.pestphp.pest.snapshotTesting

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestIcons

class SnapshotLineMarkerProvider: RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>,
    ) {
        if(! PhpPsiUtil.isOfType(element, PhpTokenTypes.IDENTIFIER)) {
            return
        }

        val functionReference = element.parent as? FunctionReferenceImpl ?: return

        if (! functionReference.isSnapshotAssertionCall) {
            return
        }

        val snapshotFiles = functionReference.snapshotFiles

        val builder = NavigationGutterIconBuilder.create(PestIcons.SNAPSHOT_ICON)
            .setTargets(snapshotFiles)
            .setTooltipText("Navigate to snapshot files")
        result.add(builder.createLineMarkerInfo(element))
    }
}
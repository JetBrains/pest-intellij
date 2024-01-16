package com.pestphp.pest.goto

import com.intellij.codeInsight.navigation.GotoTargetPresentationProvider
import com.intellij.openapi.util.NlsSafe
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import com.pestphp.pest.PestIcons
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestReference

class PestGotoTargetPresentationProvider: GotoTargetPresentationProvider {
    override fun getTargetPresentation(element: PsiElement, differentNames: Boolean): TargetPresentation? {
        if (element.isPestTestReference()) {
            @NlsSafe val pestTestName = element.getPestTestName()
            return TargetPresentation.builder(pestTestName ?: element.containingFile.name)
                .containerText(element.containingFile?.presentation?.locationString)
                .icon(PestIcons.Logo)
                .presentation()
        }
        return null
    }
}
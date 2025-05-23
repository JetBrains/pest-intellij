package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.features.customExpectations.expectationType
import com.pestphp.pest.features.customExpectations.isThisVariableInExtend

/**
 * Adds autocompletion for `$this` variable in `expect->extend`.
 */
class ThisExtendTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0223' // ȣ
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (DumbService.isDumb(psiElement.project)) return null

        if (!psiElement.isThisVariableInExtend()) return null

        return expectationType
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }
}

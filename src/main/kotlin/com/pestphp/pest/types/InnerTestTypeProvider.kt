package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.isAnyPestFunction
import com.pestphp.pest.isTestAsThisVariableInPest

/**
 * Extend `test()` type inside a test closure with types from `uses`.
 * Both `uses` from the same file, the pest config file
 * and `uses` with paths from pest config file.
 */
class InnerTestTypeProvider: ThisTypeProvider() {
  override fun getKey(): Char {
    return '\u0226' // Ȧ
  }

  override fun getType(psiElement: PsiElement): PhpType? {
      if (DumbService.isDumb(psiElement.project)) return null

      if (!psiElement.isTestAsThisVariableInPest { it.isAnyPestFunction() }) return null

      return getPestType(psiElement)
  }
}
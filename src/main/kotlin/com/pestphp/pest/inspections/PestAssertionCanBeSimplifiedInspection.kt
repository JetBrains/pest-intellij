package com.pestphp.pest.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.modcommand.ActionContext
import com.intellij.modcommand.ModPsiUpdater
import com.intellij.modcommand.PsiUpdateModCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil.capitalize
import com.intellij.openapi.util.text.StringUtil.toLowerCase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.util.startOffset
import com.intellij.refactoring.suggested.endOffset
import com.jetbrains.php.lang.PhpLangUtil
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.inspections.probablyBug.PhpDivisionByZeroInspection
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.impl.ParameterListImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.pestphp.pest.PestBundle

class PestAssertionCanBeSimplifiedInspection : PhpInspection() {
  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
    return object : PhpElementVisitor() {
      override fun visitPhpMethodReference(reference: MethodReference) {
        val methodNamePsi = reference.nameNode?.psi ?: return

        getMainParameterFromToBe(reference, methodNamePsi)?.let { mainParameter ->
          registerProblem(methodNamePsi, mainParameter, "toBe${capitalize(toLowerCase(mainParameter.text))}")
        }
        getMainParameterFromToHaveCount(reference, methodNamePsi)?.let { mainParameter ->
          registerProblem(methodNamePsi, mainParameter, "toBeEmpty")
        }
      }

      private fun getMainParameterFromToBe(reference: MethodReference, methodNameIdentifier: PsiElement): PsiElement? {
        val parameter = reference.parameterList?.getParameter("count", 0) ?: return null
        if (PhpLangUtil.equalsMethodNames(methodNameIdentifier.text, "toBe") &&
            (PhpLangUtil.isTrue(parameter) || PhpLangUtil.isFalse(parameter) || PhpLangUtil.isNull(parameter))) {
          return parameter
        }
        return null
      }

      private fun getMainParameterFromToHaveCount(reference: MethodReference, methodNameIdentifier: PsiElement): PsiElement? {
        val parameter = reference.parameterList?.getParameter("expected", 0) ?: return null
        if (PhpLangUtil.equalsMethodNames(methodNameIdentifier.text, "toHaveCount") &&
            PhpPsiUtil.isOfType(parameter, PhpElementTypes.NUMBER) &&
            PhpDivisionByZeroInspection.isZero(parameter)) {
          val functionCall = reference.classReference as? FunctionReference
          if (functionCall == null || functionCall.parameters.size != 1) return null
          val functionName = functionCall.name
          val functionParameter = functionCall.parameters.first()
          if (functionName == "expect" && functionParameter is PhpTypedElement && PhpType.isArray(functionParameter.globalType)) {
            return parameter
          }
        }
        return null
      }

      private fun registerProblem(methodNamePsi: PsiElement,
                                  parameterToRemove: PsiElement,
                                  newMethodName: String) {
        holder.problem(methodNamePsi, PestBundle.message("INSPECTION_ASSERTION_CAN_BE_SIMPLIFIED", methodNamePsi.text, newMethodName))
          .fix(PestSimplifyAssertionQuickFix(newMethodName, methodNamePsi, parameterToRemove))
          .register()
      }
    }
  }

  private class PestSimplifyAssertionQuickFix(
    private val newMethodName: String,
    methodNamePsi: PsiElement,
    parameterToRemove: PsiElement
  ) : PsiUpdateModCommandAction<PsiElement>(methodNamePsi) {
    private val parameterToRemovePointer = SmartPointerManager.getInstance(parameterToRemove.getProject())
      .createSmartPsiElementPointer(parameterToRemove)

    override fun getFamilyName() = PestBundle.message("QUICK_FIX_SIMPLIFY_ASSERTION")

    override fun invoke(context: ActionContext, methodNamePsi: PsiElement, updater: ModPsiUpdater) {
      val parameterToRemove = updater.getWritable(parameterToRemovePointer.element) ?: return
      val methodReference = methodNamePsi.parent as? MethodReference
      if (methodReference == null) return

      val methodEnd = PhpPsiUtil.findNextSiblingOfAnyType(methodNamePsi, PhpTokenTypes.chRPAREN) ?: return
      (methodReference.parameterList as? ParameterListImpl)?.removeParameter(parameterToRemove)
      val newMethodCallText = "$newMethodName(${methodReference.parameterList?.text})"
      val newMethodReference = insertIntoMethodReference(methodReference,
                                                         TextRange(methodNamePsi.startOffset, methodEnd.endOffset),
                                                         newMethodCallText,
                                                         context.project)
      methodReference.replace(newMethodReference)
    }

    private fun insertIntoMethodReference(methodReference: MethodReference,
                                          insertionRange: TextRange,
                                          insertionText: String,
                                          project: Project): MethodReference {
      val referenceRange = methodReference.textRange
      val referenceText = methodReference.text
      val referenceRelativeStart = insertionRange.startOffset - referenceRange.startOffset
      val referenceRelativeEnd = insertionRange.endOffset - referenceRange.startOffset
      val newMethodReferenceText = referenceText.substring(0, referenceRelativeStart) +
                                   insertionText +
                                   referenceText.substring(referenceRelativeEnd)
      return PhpPsiElementFactory.createMethodReference(project, newMethodReferenceText)
    }
  }
}
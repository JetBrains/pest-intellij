package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.indexers.ExpectExtendIndex
import com.pestphp.pest.isExpectation
import java.lang.UnsupportedOperationException

class ExpectExtendTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0223'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (DumbService.isDumb(psiElement.project)) return null

        // Check that our current element is a method reference
        // expect($data)->toCustomExpect()
        if (psiElement !is MethodReferenceImpl) {
            return null
        }

        val classReference = psiElement.classReference

        // Class reference should be a method or function call
        if (classReference !is FunctionReference) return null

        val type = classReference.type

        if (type.types.none { it.contains("expect") }) {
            return null
        }

        val expectMethodName = psiElement.name

        return PhpType().add("#${this.key}${classReference.signature}|$expectMethodName")
    }

    override fun complete(expression: String, project: Project): PhpType? {
        // Remove key from signature
        val signature = expression.removePrefix("#$key").substringBeforeLast('|')
        val expectMethodName = expression.substringAfterLast('|')

        val type : PhpType

        // Resolve type of the signature.
        try {
            type = signature.split('|')
                .flatMap { PhpIndex.getInstance(project).getBySignature(it) }
                .map { it.type }
                .ifEmpty { return null }
                .reduce { acc, phpType -> acc.add(phpType) }
                .global(project)
        } catch (exception: UnsupportedOperationException) {
            // This exception happens if the php type is immutable.
            return null
        }

        if (! type.isExpectation(project)) {
            return null
        }

        val expectFiles = FileBasedIndex.getInstance().getContainingFiles(
            ExpectExtendIndex.key,
            expectMethodName,
            GlobalSearchScope.allScope(project)
        )
        if (expectFiles.isEmpty()) {
            return null
        }

        return type
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }
}

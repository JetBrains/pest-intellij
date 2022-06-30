package com.pestphp.pest.features.customExpectations

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.FieldReference
import com.jetbrains.php.lang.psi.elements.MemberReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.features.FakePsiPhpMethod

class CustomExpectationTypeProvider: PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0226'
    }

    /**
     * @param element to deduce type for - using only LOCAL info. <b>THIS IS MOST CRUCIAL ASPECT TO FOLLOW</b>
     * @return type for element, null if no insight. You can return a custom signature here to be later decoded by getBySignature.
     */
    override fun getType(element: PsiElement): PhpType? {
        val reference = element as? MemberReference ?: return null

        if (reference !is FieldReference && reference !is MethodReference) return null

        val expectCall = getExpectCall(reference) ?: return null

        return PhpType().add(
            "#$key" + element.name
        )
    }

    private fun getExpectCall(reference: MemberReference, depth: Int = 50): FunctionReferenceImpl? {
        if (depth <= 0) return null

        return when (val classReference = reference.classReference) {
            is FunctionReferenceImpl -> if (classReference.name == "expect") classReference else null
            is MemberReference -> getExpectCall(classReference, depth - 1)
            else -> null
        }
    }

    /**
     * @param expression to complete - Here you can use index lookups
     * @param project well so you can reach the PhpIndex based stuff
     * @return type for element, null if no insight. You can return a custom signature here to be later decoded by getBySignature.
     */
    override fun complete(expression: String, project: Project): PhpType? {
        return null
    }

    /**
     * Here you can extend the signature lookups
     * @param expression Signature expression to decode. You can use PhpIndex.getBySignature() to look up expression internals.
     * @param visited Recursion guard: please pass this on into any phpIndex calls having same parameter
     * @param depth Recursion guard: please pass this on into any phpIndex calls having same parameter
     * @param project well so you can reach the PhpIndex
     * @return null if no match
     */
    override fun getBySignature(
        expression: String,
        visited: MutableSet<String>,
        depth: Int,
        project: Project
    ): MutableCollection<out PhpNamedElement> {
        val phpClass = PhpIndex.getInstance(project)
            .getClassesByFQN(expectationType.toString())
            .first()

        val fileBasedIndex = FileBasedIndex.getInstance()

        return fileBasedIndex
            .getAllKeys(CustomExpectationIndex.key, project)
            .mapNotNull {
                fileBasedIndex.getValues(
                    CustomExpectationIndex.key,
                    it,
                    GlobalSearchScope.allScope(project)
                )
            }.flatten()
            .flatten()
            .filter { it.name == expression }
            .map {
                FakePsiPhpMethod(
                    it,
                    phpClass
                )
            }
            .toMutableList()
    }
}
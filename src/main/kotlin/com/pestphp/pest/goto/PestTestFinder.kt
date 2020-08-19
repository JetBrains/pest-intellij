package com.pestphp.pest.goto

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinder
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.pestphp.pest.indexers.PestTestIndex
import com.pestphp.pest.isPestTestFile
import java.util.ArrayList

class PestTestFinder : TestFinder {
    override fun findClassesForTest(element: PsiElement): MutableCollection<PhpClass> {
        return PhpIndex.getInstance(element.project)
            .getClassesByNameInScope(
                element.containingFile.name.removeSuffix("Test.php"),
                GlobalSearchScope.projectScope(element.project)
            )
    }

    override fun findSourceElement(from: PsiElement): PsiElement? {
        return from.containingFile
    }

    override fun isTest(element: PsiElement): Boolean {
        return element.containingFile.isPestTestFile()
    }

    override fun findTestsForClass(element: PsiElement): MutableCollection<PsiElement> {
        val phpClass = PsiTreeUtil.getNonStrictParentOfType(element, PhpClass::class.java) ?: return arrayListOf()

        return FileBasedIndex.getInstance().getAllKeys(
            PestTestIndex.key,
            element.project
        ).filter { it.contains(phpClass.name) }
            .flatMap {
                FileBasedIndex.getInstance().getContainingFiles(
                    PestTestIndex.key,
                    it,
                    GlobalSearchScope.projectScope(element.project)
                )
            }
            .map { PsiManager.getInstance(element.project).findFile(it)!! }
            .toCollection(ArrayList())
    }
}

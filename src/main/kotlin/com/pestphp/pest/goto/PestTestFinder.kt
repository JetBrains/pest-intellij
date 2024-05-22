package com.pestphp.pest.goto

import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinder
import com.intellij.testIntegration.TestFinderHelper
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import com.pestphp.pest.indexers.key
import com.pestphp.pest.inspections.convertTestNameToSentenceCase
import com.pestphp.pest.isPestTestFile

class PestTestFinder : TestFinder {
    /**
     * @return methods if the given element is a psi child of Pest function call,
     *         classes otherwise
     */
    override fun findClassesForTest(element: PsiElement): Collection<PsiElement> {
        val classes = PhpIndex.getInstance(element.project)
            .getClassesByNameInScope(
                element.containingFile.name.removeSuffix("Test.php"),
                GlobalSearchScope.projectScope(element.project)
            )

        val testName = PsiTreeUtil.getNonStrictParentOfType(element, FunctionReference::class.java)
            ?.getPestTestName()
            ?.split(" ")
            ?.joinToString("")
            ?: return classes
        val methodsAndProximityScores = classes.flatMap { phpClass -> phpClass.ownMethods.toList() }
            .filter { method -> testName.contains(method.name, ignoreCase = true) }
            .map { method -> Pair(method, TestFinderHelper.calcTestNameProximity(method.name, testName)) }
        return if (!methodsAndProximityScores.isEmpty())
            TestFinderHelper.getSortedElements(methodsAndProximityScores, true)
        else
            classes
    }

    override fun findSourceElement(from: PsiElement): PsiElement? {
        return from.containingFile
    }

    override fun isTest(element: PsiElement): Boolean {
        if (element is PhpClass) return false
        return element.containingFile.isPestTestFile()
    }

    override fun findTestsForClass(element: PsiElement): Collection<PsiElement> {
        val parent = PsiTreeUtil.getNonStrictParentOfType(element, PhpClass::class.java, Method::class.java) ?: return arrayListOf()

        return when (parent) {
            is PhpClass -> findTestFilesForClass(parent)
            is Method -> findTestsForMethod(parent)
            else -> arrayListOf()
        }
    }

    private fun findTestsForMethod(method: Method): List<FunctionReference> {
        val phpClass = method.containingClass ?: return emptyList()
        val sentenceCaseMethodName = convertTestNameToSentenceCase(method.name)

        val testsAndProximityScores = findTestFilesForClass(phpClass)
            .flatMap { psiFile ->
                psiFile.getPestTests().mapNotNull { test ->
                    val testName = test.getPestTestName() ?: return@mapNotNull null
                    val sentenceCaseTestName = if (testName.contains(' ')) testName else convertTestNameToSentenceCase(testName)
                    if (sentenceCaseTestName.contains(sentenceCaseMethodName, ignoreCase = true)) {
                        Pair(test, TestFinderHelper.calcTestNameProximity(sentenceCaseMethodName, sentenceCaseTestName))
                    } else {
                        null
                    }
                }
            }
        return testsAndProximityScores.sortedBy { it.second }.map { it.first }
    }

    private fun findTestFilesForClass(phpClass: PhpClass): List<PsiFile> {
        return FileBasedIndex.getInstance().getAllKeys(
          key,
          phpClass.project
        ).filter { testClassName -> testClassName.contains(phpClass.name) }
            .flatMap { testClassName ->
                FileBasedIndex.getInstance().getContainingFiles(
                  key,
                  testClassName,
                  GlobalSearchScope.projectScope(phpClass.project)
                )
            }
            .mapNotNull { testFile -> phpClass.manager.findFile(testFile) }
    }
}

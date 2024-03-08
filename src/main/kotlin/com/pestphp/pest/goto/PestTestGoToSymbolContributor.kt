package com.pestphp.pest.goto

import com.intellij.ide.projectView.PresentationData
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.ProjectScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpPresentationUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.pestphp.pest.PestIcons
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import com.pestphp.pest.indexers.key

/**
 * Adds support for navigating to pest tests via the symbol searching
 */
class PestTestGoToSymbolContributor : ChooseByNameContributor {
    override fun getNames(project: Project, includeNonProjectItems: Boolean): Array<String> {
        val index = FileBasedIndex.getInstance()

        return index
            .getAllKeys(key, project)
            .flatMap {
                index.getValues(
                  key,
                  it,
                  when {
                        includeNonProjectItems -> ProjectScope.getAllScope(project)
                        else -> ProjectScope.getProjectScope(project)
                    }
                )
            }
            .flatten()
            .distinct()
            .toTypedArray()
    }

    override fun getItemsByName(
        name: String,
        pattern: String,
        project: Project,
        includeNonProjectItems: Boolean
    ): Array<NavigationItem> {
        val index = FileBasedIndex.getInstance()
        val psiManager = PsiManager.getInstance(project)

        return index
            .getAllKeys(key, project)
            .flatMap { fileName ->
                val hasName = index.getValues(
                  key,
                  fileName,
                  when {
                        includeNonProjectItems -> ProjectScope.getAllScope(project)
                        else -> ProjectScope.getProjectScope(project)
                    }
                ).flatten()
                    .contains(name)

                if (!hasName) {
                    return@flatMap emptyList()
                }

                index.getContainingFiles(
                  key,
                  fileName,
                  when {
                        includeNonProjectItems -> ProjectScope.getAllScope(project)
                        else -> ProjectScope.getProjectScope(project)
                    }
                )
            }.mapNotNull { psiManager.findFile(it) }
            .flatMap { it.getPestTests() }
            .filter { it.getPestTestName().equals(name) }
            .map { PestTestFunctionReference(it) }
            .toTypedArray()
    }

    class PestTestFunctionReference(private val functionReference: FunctionReference) : NavigationItem {
        override fun getPresentation(): ItemPresentation {
            val location = PhpPresentationUtil.getPresentablePathForFile(
                functionReference.containingFile.virtualFile,
                functionReference.project
            )

            return PresentationData(
                functionReference.getPestTestName(),
                location,
                PestIcons.Logo,
                null,
            )
        }

        override fun navigate(requestFocus: Boolean) = functionReference.navigate(requestFocus)

        override fun canNavigate(): Boolean = functionReference.canNavigate()

        override fun canNavigateToSource(): Boolean = canNavigateToSource()

        override fun getName(): String? {
            return functionReference.getPestTestName()
        }
    }
}
package com.pestphp.pest.goto

import com.intellij.ide.projectView.PresentationData
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import com.jetbrains.php.PhpPresentationUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestIcons
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import com.pestphp.pest.indexers.PestTestIndex
import javax.swing.Icon

class PestTestGoToSymbolContributor: ChooseByNameContributor {
    override fun getNames(project: Project, includeNonProjectItems: Boolean): Array<String> {
        val index = FileBasedIndex.getInstance()

        return index
            .getAllKeys(PestTestIndex.key, project)
            .flatMap {
                index.getValues(
                    PestTestIndex.key,
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
            .getAllKeys(PestTestIndex.key, project)
            .flatMap { fileName ->
                val hasName = index.getValues(
                    PestTestIndex.key,
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
                    PestTestIndex.key,
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

    class PestTestFunctionReference(val b: FunctionReference): FunctionReference by b {
        override fun getPresentation(): ItemPresentation {
            val location = PhpPresentationUtil.getPresentablePathForFile(
                b.containingFile.virtualFile,
                b.project
            )

            return PresentationData(
                b.getPestTestName(),
                location,
                PestIcons.LOGO,
                null,
            )
        }

        override fun getName(): String? {
            return b.getPestTestName()
        }

        override fun getIcon(flags: Int): Icon {
            return PestIcons.LOGO
        }
    }
}
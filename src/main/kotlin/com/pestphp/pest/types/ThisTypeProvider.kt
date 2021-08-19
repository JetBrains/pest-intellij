package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.PestSettings
import com.pestphp.pest.getUsesPhpType
import com.pestphp.pest.isAnyPestFunction
import com.pestphp.pest.isThisVariableInPest

/**
 * Extend `$this` type with types from `uses`.
 * Both `uses` from the same file, the pest config file
 * and `uses` with paths from pest config file.
 */
class ThisTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0221'
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (DumbService.isDumb(psiElement.project)) return null

        if (
            ((psiElement as? FunctionReferenceImpl)?.isAnyPestFunction() != true) &&
            !psiElement.isThisVariableInPest { it.isAnyPestFunction() }
        ) return null

        val virtualFile = psiElement.containingFile?.originalFile?.virtualFile ?: return null

        val config = PestSettings.getInstance(psiElement.project).getPestConfiguration(psiElement.project)

        val baseDir = (psiElement.project.guessProjectDir() ?: return config.baseTestType)
        val relativePath = VfsUtil.getRelativePath(virtualFile, baseDir) ?: return config.baseTestType

        val result = PhpType().add(config.baseTestType)

        config.pathsClasses.forEach { (path, type) ->
            if (relativePath.startsWith(path)) {
                result.add(type)
            }
        }

        PsiTreeUtil.findChildrenOfType(psiElement.containingFile, FunctionReferenceImpl::class.java)
            .filter { it.name == "uses" }
            .mapNotNull { it.getUsesPhpType() }
            .forEach { result.add(it) }

        return result
    }

    override fun complete(s: String, project: Project): PhpType? {
        return null
    }

    override fun getBySignature(s: String, set: Set<String>, i: Int, project: Project): Collection<PhpNamedElement?> {
        return emptyList()
    }
}

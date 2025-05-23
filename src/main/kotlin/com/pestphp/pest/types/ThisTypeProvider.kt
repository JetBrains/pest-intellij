package com.pestphp.pest.types

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4
import com.pestphp.pest.*
import java.nio.file.FileSystems
import kotlin.io.path.Path

/**
 * Extend `$this` type with types from `uses`.
 * Both `uses` from the same file, the pest config file
 * and `uses` with paths from pest config file.
 */
open class ThisTypeProvider : PhpTypeProvider4 {
    override fun getKey(): Char {
        return '\u0221' // ȡ
    }

    override fun getType(psiElement: PsiElement): PhpType? {
        if (DumbService.isDumb(psiElement.project)) return null

        if (
            ((psiElement as? FunctionReferenceImpl)?.isAnyPestFunction() != true) &&
            !psiElement.isThisVariableInPest { it.isAnyPestFunction() }
        ) return null

        return getPestType(psiElement)
    }

    protected fun getPestType(psiElement: PsiElement): PhpType? {
        val virtualFile = psiElement.containingFile?.originalFile?.virtualFile ?: return null

        val config = PestSettings.getInstance(psiElement.project).getPestConfiguration(psiElement.project, virtualFile)

        val baseDir = (psiElement.project.guessProjectDir() ?: return config.baseTestType)
        val relativePath = VfsUtil.getRelativePath(virtualFile, baseDir) ?: return config.baseTestType

        val result = PhpType().add(config.baseTestType)
        val defaultFileSystem = FileSystems.getDefault()

        config.pathsClasses.forEach { (path, type) ->
            FileUtil.toCanonicalPath(path)?.let { normalizedPathForMatching ->
                if (defaultFileSystem.getPathMatcher("glob:$normalizedPathForMatching**").matches(Path(relativePath))) {
                    result.add(type)
                }
            }
        }

        psiElement.containingFile.getRoot()
            .filterIsInstance<FunctionReference>()
            .mapNotNull { it.getPestConfigurationPhpType() }
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

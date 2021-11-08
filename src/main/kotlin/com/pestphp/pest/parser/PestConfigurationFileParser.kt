package com.pestphp.pest.parser

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.PathUtil
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpFilePathUtils
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestSettings
import com.pestphp.pest.getUsesPhpType
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

class PestConfigurationFileParser(private val settings: PestSettings) {
    fun parse(project: Project): PestConfigurationFile {
        val baseDir = project.guessProjectDir() ?: return defaultConfig

        val pestFile = VirtualFileManager.getInstance().findFileByUrl(baseDir.url + "/" + settings.pestFilePath)
            ?: return defaultConfig

        val psiFile = PsiManager.getInstance(project).findFile(pestFile) as? PhpFile ?: return defaultConfig

        return CachedValuesManager.getCachedValue(psiFile, cacheKey) {
            var baseType = PhpType().add("\\PHPUnit\\Framework\\TestCase")
            val inPaths = mutableListOf<Pair<String, PhpType>>()
            val testsPath = settings.pestFilePath.replaceAfterLast('/', "")

            psiFile.acceptChildren(
                Visitor { type, inPath, fullPath ->
                    if (fullPath && inPath != null) {
                        inPaths.add(Pair(inPath.replaceBefore(testsPath, ""), type))
                    } else if (inPath != null) {
                        inPaths.add(Pair(testsPath + inPath, type))
                    } else {
                        baseType = type
                    }
                }
            )

            CachedValueProvider.Result.create(PestConfigurationFile(baseType, inPaths), psiFile)
        } ?: defaultConfig
    }

    private class Visitor(private val collect: (PhpType, String?, Boolean) -> Unit) : PsiRecursiveElementWalkingVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is MethodReference) {
                visitInReference(element)
                return
            } else if (element is FunctionReferenceImpl) {
                if (element.name == "uses") {
                    collect(element.getUsesPhpType() ?: return, null, false)
                }
                return
            }

            super.visitElement(element)
        }

        private fun visitInReference(inReference: MethodReference) {
            var reference = inReference
            var usesType: PhpType? = null
            while (true) {
                val ref = reference.classReference ?: return

                if (ref is MethodReference) {
                    reference = ref
                } else if (ref is FunctionReferenceImpl) {
                    if (ref.name == "uses") {
                        usesType = ref.getUsesPhpType()
                    }

                    break
                } else {
                    return
                }
            }

            if (usesType == null) return

            inReference.parameters.forEach {
                when (it) {
                    is StringLiteralExpression -> collect(usesType, PhpFilePathUtils.getFileName(it), false)
                    is ConcatenationExpression -> collect(
                        usesType,
                        Path(PhpFilePathUtils.getFileName(it)!!).pathString,
                        true
                    )
                }
            }
        }
    }

    companion object {
        private val defaultConfig = PestConfigurationFile(
            PhpType().add("\\PHPUnit\\Framework\\TestCase"),
            emptyList()
        )

        private val cacheKey = Key<CachedValue<PestConfigurationFile>>("com.pestphp.pest_configuration")
    }
}

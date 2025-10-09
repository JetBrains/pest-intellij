package com.pestphp.pest.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.findParentOfType
import com.jetbrains.php.lang.PhpLangUtil
import com.jetbrains.php.lang.inspections.PhpInspection
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.pestphp.pest.PestBundle
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestFile

class MissingScreenshotSnapshotInspection : PhpInspection() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PhpElementVisitor() {
            override fun visitPhpMethodReference(reference: MethodReference) {
                val methodName = reference.name ?: return
                if (!PhpLangUtil.equalsMethodNames(methodName, "assertScreenshotMatches")) return

                if (!reference.containingFile.isPestTestFile()) return

                val pestCall = reference.findParentOfType<FunctionReferenceImpl>() ?: return
                val testName = pestCall.getPestTestName() ?: return

                if (!snapshotExists(reference, testName)) {
                    val namePsi = reference.nameNode?.psi ?: reference
                    holder.registerProblem(
                        namePsi,
                        PestBundle.message("INSPECTION_MISSING_SCREENSHOT_SNAPSHOT")
                    )
                }
            }
        }
    }

    private fun snapshotExists(context: MethodReference, testName: String): Boolean {
        val file = context.containingFile.originalFile.virtualFile ?: return false
        val testsRoot = getTestsRoot(file) ?: return false

        val relativePath = VfsUtil.getRelativePath(file.parent, testsRoot) ?: return false
        val snapshotPath = ".pest/snapshots/$relativePath/${file.nameWithoutExtension}"
        val expectedDir = testsRoot.findFileByRelativePath(snapshotPath) ?: return false

        val normalizedTestName = testName.replace("_", "__").replace(Regex("[^a-zA-Z0-9→]"), "_")
        return expectedDir.children?.any { it.name.matches("${normalizedTestName}(__\\d+)?\\.snap".toRegex()) } == true
    }

    private fun getTestsRoot(file: VirtualFile): VirtualFile? {
        var currentDir = file.parent
        while (currentDir != null && currentDir.name != "tests") {
            currentDir = currentDir.parent
        }
        return currentDir
    }
}

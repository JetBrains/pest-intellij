package com.pestphp.pest.snapshotTesting

import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.codeInsight.controlFlow.PhpControlFlowUtil
import com.jetbrains.php.codeInsight.controlFlow.PhpInstructionProcessor
import com.jetbrains.php.codeInsight.controlFlow.instructions.PhpCallInstruction
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestReference

val snapshotAssertionNames = listOf(
    "assertMatchesSnapshot",
    "assertMatchesFileHashSnapshot",
    "assertMatchesFileSnapshot",
    "assertMatchesHtmlSnapshot",
    "assertMatchesJsonSnapshot",
    "assertMatchesObjectSnapshot",
    "assertMatchesTextSnapshot",
    "assertMatchesXmlSnapshot",
    "assertMatchesYamlSnapshot",
)

val FunctionReferenceImpl.isSnapshotAssertionCall: Boolean
    get() {
        return snapshotAssertionNames.contains(this.name)
    }

val FunctionReferenceImpl.snapshotFiles: List<PsiFile>
    get() {
        val pestBody = this.parentOfType<FunctionImpl>() ?: return emptyList()

        // Make sure we are inside a pest test
        val pestTestReference = pestBody.parent?.parent?.parent ?: return emptyList()

        if (!pestTestReference.isPestTestReference()) {
            return emptyList()
        }

        val projectRoot = this.project.basePath ?: return emptyList()
        val snapshotDirectory = LocalFileSystem.getInstance()
            .findFileByPath("$projectRoot/tests/__snapshots__") ?: return emptyList()
        val testFileName = this.containingFile.name.removeSuffix(".php")
        val testName = pestTestReference.getPestTestName() ?: return emptyList()

        val snapshotFiles = mutableListOf<PsiFile>()
        val snapshotCalls = pestBody.getSnapshotCallNumber(this)


        ProjectFileIndex.getInstance(this.project)
            .iterateContentUnderDirectory(
                snapshotDirectory
            ) {
                val psiFile = PsiManager.getInstance(this.project)
                    .findFile(it) ?: return@iterateContentUnderDirectory true

                if (!psiFile.isSnapshotFile(
                        testName,
                        testFileName,
                        snapshotCalls
                    )
                ) {
                    return@iterateContentUnderDirectory true
                }

                snapshotFiles.add(psiFile)
                true
            }

        return snapshotFiles
    }

private fun PsiFile.isSnapshotFile(testName: String, testFileName: String, snapshotCall: Int): Boolean {
    val snapshotFileName = this.virtualFile.nameWithoutExtension
    this.virtualFile.extension ?: return false

    val testNameUnderscore = testName.replace(' ', '_')

    if (!snapshotFileName.startsWith("${testFileName}__$testNameUnderscore")) {
        return false
    }

    if (!snapshotFileName.endsWith("__$snapshotCall")) {
        return false
    }

    return true
}

private fun FunctionImpl.getSnapshotCallNumber(snapshotFunctionReference: FunctionReferenceImpl): Int {
    var snapshotCalls = 0
    val processor: PhpInstructionProcessor = object : PhpInstructionProcessor() {
        override fun processPhpCallInstruction(instruction: PhpCallInstruction): Boolean {
            val functionReference = instruction.functionReference

            if (functionReference !is FunctionReferenceImpl) {
                return super.processPhpCallInstruction(instruction)
            }

            if (!functionReference.isSnapshotAssertionCall) {
                return super.processPhpCallInstruction(instruction)
            }
            snapshotCalls++

            if (PsiManager.getInstance(functionReference.project).areElementsEquivalent(
                    functionReference,
                    snapshotFunctionReference
                )
            ) {
                return false
            }

            return super.processPhpCallInstruction(instruction)
        }
    }

    val flow = controlFlow
    PhpControlFlowUtil.processSuccessors(flow.entryPoint, false, processor)
    return snapshotCalls
}
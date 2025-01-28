package com.pestphp.pest

import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import java.io.File

@Suppress("UnnecessaryAbstractClass")
@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest")
abstract class PestLightCodeFixture : BasePlatformTestCase() {
    override fun getBasePath() = "src/test/resources/com/pestphp/pest"

    override fun setUp() {
        super.setUp()
        val pestStubsFile = File("${this@PestLightCodeFixture.basePath}/stubs.php")
        if (pestStubsFile.exists()) {
            myFixture.copyFileToProject(pestStubsFile.absolutePath, "stubs.php")
        }
    }

    private val testNameSeparator = '#'

    protected fun assertCompletion(vararg shouldContain: String) {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings ?: return fail("empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun assertAllCompletion(vararg shouldContain: String) {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings ?: return fail("empty completion result")

        assertEquals(shouldContain.toList(), strings)
    }

    protected fun assertNoCompletion() {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings

        assertNullOrEmpty(strings)
    }

    protected fun createPestFrameworkConfiguration(): PhpTestFrameworkConfiguration? {
        val configuration = PhpTestFrameworkSettingsManager
            .getInstance(myFixture.project)
            .getOrCreateByInterpreter(PestFrameworkType.instance, PhpInterpreter(), null, false)
        configuration?.executablePath = "randomPath"

        return configuration
    }

    protected fun configureByPhpCode(code: String) {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php $code")
    }

    private fun configureByDirectory(relativePathToDirectory: String) {
        val fromFile = File("$testDataPath/$relativePathToDirectory")
        if (fromFile.exists() && fromFile.isDirectory) {
            fromFile.listFiles()?.forEach { file ->
                val relativePath = FileUtil.toSystemIndependentName(file.path).removePrefix("$testDataPath/")
                myFixture.copyFileToProject(relativePath)
            }
        }
    }

    protected fun configureByFile(): PsiFile {
        val path = getFileNameBeforeRelativePath()
        configureByDirectory(path)
        return myFixture.configureByFile(path)
    }

    private fun getFullPath(basePath: String, relativePath: String): String {
        return "$basePath/$relativePath"
    }

    protected fun getFileNameBeforeRelativePath(): String {
        return getTestName(false).let { testPath ->
            val phpTestPath = "$testPath.php"
            if (phpTestPath.contains(testNameSeparator)) {
                phpTestPath.replace(testNameSeparator, File.separatorChar)
            } else {
                phpTestPath
            }
        }
    }

    protected fun getFileBeforeFullPath(): String {
        return getFullPath(testDataPath, getFileNameBeforeRelativePath())
    }
}

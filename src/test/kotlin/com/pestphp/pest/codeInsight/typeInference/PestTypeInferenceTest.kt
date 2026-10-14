package com.pestphp.pest.codeInsight.typeInference

import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.util.Segment
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.PhpLangUtil
import com.jetbrains.php.lang.psi.PhpPsiUtil
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.pestphp.pest.PestSettings
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.util.regex.Pattern

@TestApplication
class PestTypeInferenceTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private lateinit var pestFilePathBackup: String
    private lateinit var testMethodName: String

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/codeInsight/typeInference")
        testMethodName = testInfo.testMethod.get().name
        pestFilePathBackup = PestSettings.getInstance(myFixture.project).pestFilePath
    }

    @AfterEach
    fun tearDown() {
        PestSettings.getInstance(myFixture.project).pestFilePath = pestFilePathBackup
    }

    private fun doTest(block: () -> PsiFile) {
        PestSettings.getInstance(myFixture.project).pestFilePath = "Pest.php"
        val pestPhpFile = block()
        timeoutRunBlocking {
            withContext(Dispatchers.EDT) {
                myFixture.openFileInEditor(pestPhpFile.virtualFile)
            }
        }
        doTypeTest()
    }

    private fun doTypeTest() {
        val file = PestTestUtil.configureByFile(myFixture, testMethodName)
        val originalFileText = runReadActionBlocking { file.text }
        val fileTextWithExtractedTypes = StringBuilder()
        val rangesToTypes = extractRangesToTypes(originalFileText, fileTextWithExtractedTypes)
        val fileWithExtractedTypes = replaceContentOfConfiguredFile(fileTextWithExtractedTypes)
        val text = runReadActionBlocking { fileWithExtractedTypes.text }
        var lastOffset = 0
        val contentWithActualTypes = StringBuilder()
        for ((segment, stringTypes) in rangesToTypes.entries) {
            val actualType = getType(segment.startOffset, segment.endOffset)
            if (actualType.hasUnknown()) {
                actualType.add(PhpType.MIXED)
            }
            val diffType = constructDiffType(actualType, stringTypes)
            contentWithActualTypes.append(text, lastOffset, segment.startOffset)
            appendActualType(text, contentWithActualTypes, segment, diffType)
            lastOffset = segment.endOffset
        }
        contentWithActualTypes.append(text.substring(lastOffset))
        assertEquals(originalFileText, contentWithActualTypes.toString(), "Types are not matched")
    }

    private fun replaceContentOfConfiguredFile(fileTextWithExtractedTypes: StringBuilder): PsiFile = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            WriteAction.run<RuntimeException> { myFixture.editor.getDocument().setText(fileTextWithExtractedTypes.toString()) }
            PsiDocumentManager.getInstance(myFixture.project).commitAllDocuments()
            myFixture.file
        }
    }

    private fun getType(start: Int, end: Int): PhpType = runReadActionBlocking {
        val expression = PhpPsiUtil.findElementOfClassAtRange(
            myFixture.file, start, end,
            PhpPsiElement::class.java, true, true
        )
        assertNotNull(expression)
        assertTrue(expression is PhpTypedElement, expression!!.text)
        (expression as PhpTypedElement?)!!.globalType
    }

    @Test
    fun testThisInInnerClosure(): Unit = doTest {
        myFixture.addFileToProject("Pest.php", "<?php")
    }

    @Test
    fun `testThisInSubproject#Test`(): Unit = doTest {
        PestSettings.getInstance(myFixture.project).pestFilePath = "ThisInSubproject/Pest.php"
        myFixture.addFileToProject(
            "ThisInSubproject/TestCase.php", """
            <?php

            abstract class TestCase { }
            """
        )
        myFixture.addFileToProject(
            "ThisInSubproject/Pest.php", """
            <?php
                        
            uses(TestCase::class)->in("./");
        """.trimIndent()
        )
    }
}

private const val CLOSING_TYPE_TAG = "</type>"
private val PATTERN = Pattern.compile("<type value=\"([^\"]*)\">")

private fun extractRangesToTypes(text: String, contentWithExtractedTypes: java.lang.StringBuilder): LinkedHashMap<Segment, List<String>> {
    val res = LinkedHashMap<Segment, List<String>>()
    var lastOffset = 0
    val matcher = PATTERN.matcher(text)
    while (matcher.find()) {
        contentWithExtractedTypes.append(text, lastOffset, matcher.start())
        val closingTagIndexOf = text.indexOf(CLOSING_TYPE_TAG, lastOffset)
        val start = contentWithExtractedTypes.length
        contentWithExtractedTypes.append(text, matcher.end(), closingTagIndexOf)
        val end = contentWithExtractedTypes.length
        res[TextRange.create(start, end)] = StringUtil.split(matcher.group(1), "|")
        lastOffset = closingTagIndexOf + CLOSING_TYPE_TAG.length
    }
    contentWithExtractedTypes.append(text.substring(lastOffset))
    return res
}

private fun constructDiffType(type: PhpType, strings: List<String>): String {
    return type.types.asSequence()
        .filter { t -> !t.startsWith("?") }
        .filter { qualifiedName -> PhpLangUtil.isFqn(qualifiedName!!) }
        .map { fqn -> toPresentableFQN(fqn) }
        .sortedBy { fqn -> strings.indexOf(fqn) }
        .joinToString("|")
}

private fun toPresentableFQN(fqn: String): String {
    return if (PhpLangUtil.isGlobalNamespaceFQN(PhpLangUtil.getParentNamespaceFQN(fqn))) PhpLangUtil.toPresentableFQN(fqn) else fqn
}

private fun appendActualType(text: String, contentWithActualTypes: java.lang.StringBuilder, segment: Segment, diffType: String) {
    contentWithActualTypes.append("<type value=\"").append(diffType).append("\">")
    contentWithActualTypes.append(text, segment.startOffset, segment.endOffset)
    contentWithActualTypes.append(CLOSING_TYPE_TAG)
}
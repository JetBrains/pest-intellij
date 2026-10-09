package com.pestphp.pest.customExpectations

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.FileContentImpl
import com.jetbrains.php.lang.PhpFileType
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.features.customExpectations.CustomExpectationIndex
import com.pestphp.pest.features.customExpectations.collectCustomExpectations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
class CustomExpectationIndexTextGateTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, PestTestUtil.getTestDataPath())
    }

    @Test
    fun testExtendCall() {
        assertKeys(setOf("toBeOne"), $$"<?php expect()->extend('toBeOne', function () { return $this->toBe(1); });")
    }

    @Test
    fun testExtendCallWithSpacesAndComment() {
        assertKeys(setOf("toBeOne"), $$"<?php expect() -> /* comment */ extend('toBeOne', fn () => $this->toBe(1));")
    }

    @Test
    fun testNamespacedExpectCall() {
        assertSameKeys($$"<?php \\Pest\\expect()->extend('toBeOne', fn () => $this->toBe(1));")
    }

    @Test
    fun testExtendCallAfterSimilarWords() {
        assertKeys(setOf("toBeOne"), $$"""
            <?php
            class UserTest extends TestCase {
                public function testName() {
                    $this->expectException(LogicException::class);
                }
            }
            expect()->extend('toBeOne', fn () => $this->toBe(1));
            """.trimIndent())
    }

    @Test
    fun testTestCaseWithoutCustomExpectations() {
        assertKeys(emptySet(), $$"""
            <?php
            class UserTest extends TestCase {
                public function testName() {
                    $this->expectException(LogicException::class);
                }
            }
            """.trimIndent())
    }

    private fun assertKeys(expectedKeys: Set<String>, text: String) {
        assertEquals(expectedKeys, assertSameKeys(text))
    }

    private fun assertSameKeys(text: String): Set<String> {
        val file = LightVirtualFile("gate.php", PhpFileType.INSTANCE, text)
        val ungatedKeys = mapKeys(file, ::collectCustomExpectations)
        val gatedKeys = mapKeys(file, CustomExpectationIndex().indexer::map)
        assertEquals(ungatedKeys, gatedKeys)
        return ungatedKeys
    }

    private fun mapKeys(file: LightVirtualFile, mapper: (FileContent) -> Map<String, *>): Set<String> =
        runReadActionBlocking {
            mapper(FileContentImpl.createByFile(file, myFixture.project)).keys
        }
}

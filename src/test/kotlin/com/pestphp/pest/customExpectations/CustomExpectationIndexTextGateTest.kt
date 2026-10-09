package com.pestphp.pest.customExpectations

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.FileContentImpl
import com.jetbrains.php.lang.PhpFileType
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.features.customExpectations.CustomExpectationIndex
import com.pestphp.pest.features.customExpectations.collectCustomExpectations

class CustomExpectationIndexTextGateTest : PestLightCodeFixture() {
    fun testExtendCall() {
        assertKeys(setOf("toBeOne"), $$"<?php expect()->extend('toBeOne', function () { return $this->toBe(1); });")
    }

    fun testExtendCallWithSpacesAndComment() {
        assertKeys(setOf("toBeOne"), $$"<?php expect() -> /* comment */ extend('toBeOne', fn () => $this->toBe(1));")
    }

    fun testNamespacedExpectCall() {
        assertSameKeys($$"<?php \\Pest\\expect()->extend('toBeOne', fn () => $this->toBe(1));")
    }

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

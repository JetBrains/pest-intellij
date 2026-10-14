package com.pestphp.pest.surrounders

import com.intellij.testFramework.junit5.TestApplication
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Test

@TestApplication
class ExpectStatementSurrounderTest: SurroundTestCase() {
    override fun getTestDataPath(): String = "${PestTestUtil.getTestDataPath()}/surrounders"

    @Test
    fun testSurroundSingleElements() {
        doTest(
            ExpectStatementSurrounder(),
            """
                <?php
                
                it('basic', function () {
                    <selection>true</selection>
                });
            """.trimIndent(),
            """
                <?php
                
                it('basic', function () {
                    <selection>expect(true)</selection>
                });
            """.trimIndent()
        )
    }

    @Test
    fun testSurroundMultipleElements() {
        doTest(
            ExpectStatementSurrounder(),
            """
                <?php
                
                it('basic', function () {
                    <selection>100 === 200</selection>
                });
            """.trimIndent(),
            """
                <?php
                
                it('basic', function () {
                    <selection>expect(100 === 200)</selection>
                });
            """.trimIndent()
        )
    }

    @Test
    fun testNoSurroundOutsideTest() {
        doTest(
            ExpectStatementSurrounder(),
            """
                <?php
                
                <selection>true</selection>
            """.trimIndent(),
            """
                <?php
                
                true
            """.trimIndent()
        )
    }

    @Test
    fun testNoSurroundOnTestItself() {
        doTest(
            ExpectStatementSurrounder(),
            """
                <?php
                
                <selection>it('basic', function () {
                    expect(true)->toBeTrue();
                });</selection>
            """.trimIndent(),
            """
                <?php
                
                it('basic', function () {
                    expect(true)->toBeTrue();
                });
            """.trimIndent()
        )
    }

    @Test
    fun testSurroundInsideDescribeBlock() {
        doTest(
            ExpectStatementSurrounder(),
            """
                <?php

                describe('sum', function () {
                    it('adds numbers', function () {
                        <selection>1 + 1</selection>
                    });
                });
            """.trimIndent(),
            """
                <?php

                describe('sum', function () {
                    it('adds numbers', function () {
                        <selection>expect(1 + 1)</selection>
                    });
                });
            """.trimIndent()
        )
    }
}
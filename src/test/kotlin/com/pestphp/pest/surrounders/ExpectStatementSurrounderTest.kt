package com.pestphp.pest.surrounders

class ExpectStatementSurrounderTest: SurroundTestCase() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/surrounders"
    }

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
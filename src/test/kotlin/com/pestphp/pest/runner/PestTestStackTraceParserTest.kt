package com.pestphp.pest.runner

import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.configuration.PestLocationProvider

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/runner/pestTestStacktraceParser")
class PestTestStackTraceParserTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/runner/pestTestStacktraceParser"
    }

    private fun createStackTraceParser(url: String, stacktrace: String?, message: String?): PestTestStackTraceParser {
        val locator = PestLocationProvider(PhpPathMapper.create(project), project, myFixture.testDataPath)
        return parse(url, stacktrace, message, locator, project)
    }

    fun testNull() {
        val stackTraceParser = createStackTraceParser("pest_qn://path/to/test.php::myTest", null, null)
        assertNull(stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    fun testEmptyLine() {
        val stackTraceParser = createStackTraceParser("pest_qn://path/to/test.php::myTest", "", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    fun testWithoutTestMention() {
        val stackTraceParser =
            createStackTraceParser("pest_qn://path/to/test.php::myTest", "at /path/to/test2.php:14\nat /path/to/test3.php:11", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    fun testOneLine() {
        configureByFile()
        val stackTraceParser =
            createStackTraceParser("pest_qn://" + getFileBeforeFullPath() + "::myTest", getFileBeforeFullPath() + ":4", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(4, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertEquals("   expect(1)->toBe(2);", stackTraceParser.failedMethodName)
    }

    fun testMultiline() {
        configureByFile()
        val stackTraceParser = createStackTraceParser(
            "pest_qn://" + getFileBeforeFullPath() + "::\\MyTest::test",
            getFileBeforeFullPath() + ":12\n " +
                getFileBeforeFullPath() + ":8\n" +
                getFileBeforeFullPath() + ":4", null
        )
        assertEquals(getFileBeforeFullPath() + ":12", stackTraceParser.errorMessage)
        assertEquals(4, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertEquals("    foo();", stackTraceParser.failedMethodName)
    }


    fun testWrongLineNumber() {
        configureByFile()
        val stackTraceParser =
            createStackTraceParser("pest_qn://" + getFileBeforeFullPath() + "::myTest", getFileBeforeFullPath() + ":seven", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    fun testOutRangeLineNumber() {
        configureByFile()
        val stackTraceParser =
            createStackTraceParser("pest_qn://" + getFileBeforeFullPath() + "::myTest", getFileBeforeFullPath() + ":10", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(10, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }
}
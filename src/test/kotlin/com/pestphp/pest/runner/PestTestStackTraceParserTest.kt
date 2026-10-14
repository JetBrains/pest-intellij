package com.pestphp.pest.runner

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.intellij.util.PathMappingSettings
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.jetbrains.php.util.pathmapper.PhpRemotePathMapper
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.configuration.PestLocationProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/runner/pestTestStacktraceParser")
class PestTestStackTraceParserTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private lateinit var testMethodName: String

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/runner/pestTestStacktraceParser")
        testMethodName = testInfo.testMethod.get().name
    }

    private fun createStackTraceParser(url: String, stacktrace: String?, message: String?): PestTestStackTraceParser {
        val locator = PestLocationProvider(PhpPathMapper.create(myFixture.project), myFixture.project, myFixture.testDataPath)
        return runReadActionBlocking { parse(url, stacktrace, message, locator, myFixture.project) }
    }

    private fun createRemoteStackTraceParser(url: String, stacktrace: String?, message: String?): PestTestStackTraceParser {
        val locator = PestLocationProvider(PhpRemotePathMapper.create(listOf(PathMappingSettings.PathMapping(getFileBeforeFullPath(), "/opt/project"))), myFixture.project, myFixture.testDataPath)
        return runReadActionBlocking { parse(url, stacktrace, message, locator, myFixture.project) }
    }

    @Test
    fun testNull() {
        val stackTraceParser = createStackTraceParser("pest_qn://path/to/test.php::myTest", null, null)
        assertNull(stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    @Test
    fun testEmptyLine() {
        val stackTraceParser = createStackTraceParser("pest_qn://path/to/test.php::myTest", "", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    @Test
    fun testWithoutTestMention() {
        val stackTraceParser =
            createStackTraceParser("pest_qn://path/to/test.php::myTest", "at /path/to/test2.php:14\nat /path/to/test3.php:11", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    @Test
    fun testOneLine() {
        PestTestUtil.configureByFile(myFixture, testMethodName)
        val stackTraceParser =
            createStackTraceParser("pest_qn://" + getFileBeforeFullPath() + "::myTest", getFileBeforeFullPath() + ":4", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(4, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertEquals("   expect(1)->toBe(2);", stackTraceParser.failedMethodName)
    }

    @Test
    fun testMultiline() {
        PestTestUtil.configureByFile(myFixture, testMethodName)
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

    @Test
    fun testWrongLineNumber() {
        PestTestUtil.configureByFile(myFixture, testMethodName)
        val stackTraceParser =
            createStackTraceParser("pest_qn://" + getFileBeforeFullPath() + "::myTest", getFileBeforeFullPath() + ":seven", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(-1, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    @Test
    fun testOutRangeLineNumber() {
        PestTestUtil.configureByFile(myFixture, testMethodName)
        val stackTraceParser =
            createStackTraceParser("pest_qn://" + getFileBeforeFullPath() + "::myTest", getFileBeforeFullPath() + ":10", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(10, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertNull(stackTraceParser.failedMethodName)
    }

    @Test
    fun testOneLineRemote() {
        PestTestUtil.configureByFile(myFixture, testMethodName)
        val stackTraceParser =
            createRemoteStackTraceParser("pest_qn://" + getFileBeforeFullPath() + "::myTest", getFileBeforeFullPath() + ":4", "error")
        assertEquals("error", stackTraceParser.errorMessage)
        assertEquals(4, stackTraceParser.failedLine)
        assertNull(stackTraceParser.topLocationLine)
        assertEquals("   expect(1)->toBe(2);", stackTraceParser.failedMethodName)
    }

    private fun getFileBeforeFullPath(): String = PestTestUtil.getFileBeforeFullPath(myFixture, testMethodName)
}

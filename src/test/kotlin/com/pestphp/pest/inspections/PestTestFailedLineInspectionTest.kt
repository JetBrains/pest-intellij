package com.pestphp.pest.inspections

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.execution.TestStateStorage
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.EDT
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.util.Date

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/inspections/pestTestFailedLine")
class PestTestFailedLineInspectionTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private lateinit var testMethodName: String

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/inspections/pestTestFailedLine")
        testMethodName = testInfo.testMethod.get().name
        myFixture.enableInspections(PestTestFailedLineInspection::class.java)
    }

    /**
     * Returns the path of the module source root, relative to the project base path.
     */
    private val sourceRootLocation: String
        get() = myFixture.tempDirPath.removePrefix(myFixture.project.basePath.orEmpty()).removePrefix("/")

    private fun writeTestState(record: TestStateStorage.Record) {
        TestStateStorage.getInstance(myFixture.project).writeState("pest_qn://$sourceRootLocation/${PestTestUtil.getFileNameBeforeRelativePath(testMethodName)}::myTest", record)
    }

    private fun writeTestStateWithDataset(record: TestStateStorage.Record, dataset: String) {
        TestStateStorage.getInstance(myFixture.project).writeState("pest_qn://$sourceRootLocation/${PestTestUtil.getFileNameBeforeRelativePath(testMethodName)}::myTest with data set $dataset", record)
    }

    private fun doTest(record: TestStateStorage.Record) {
        writeTestState(record)
        PestTestUtil.configureByFile(myFixture, testMethodName)
        checkHighlighting()
    }

    @Test
    fun testFailedOneLine() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    @Test
    fun testMismatchLine() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(3);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    @Test
    fun testOutRange() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 12,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    @Test
    fun testTypeBefore() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
        myFixture.type("\n echo 1;")
        val highlightInfos = doWarningHighlighting()
        assertEquals(1, highlightInfos.size)
        assertEquals(76, highlightInfos[0].startOffset)
        assertEquals(95, highlightInfos[0].endOffset)
    }

    @Test
    fun testTypeInside() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
        myFixture.type("Not")
        val highlightInfos = doWarningHighlighting()
        assertEquals(1, highlightInfos.size)
        assertEquals(63, highlightInfos[0].startOffset)
        assertEquals(85, highlightInfos[0].endOffset)
    }

    @Test
    fun testAnonymousFunction() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 7,
            "    });",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    @Test
    fun testLambdaFunction() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 6,
            "    );",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    @Test
    fun testSingleLeafElementReported() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 8,
            "    );",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    @Test
    fun testMultipleStatementsInOneLine() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(1); expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    @Test
    fun testWithDataSetAndKeys() {
        val secondDatasetRecord = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(\$a);",
            "Failed asserting that 1 is identical to 2.", null
        )
        writeTestStateWithDataset(secondDatasetRecord, "\"dataset \"\"second dataset\"")
        val thirdDatasetRecord = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(\$a);",
            "Failed asserting that 1 is identical to 3.", null
        )
        writeTestStateWithDataset(thirdDatasetRecord, "\"dataset \"\"third dataset\"")
        val methodRecord = TestStateStorage.Record(6, Date(), 0, -1, "", "", null)
        doTest(methodRecord)
    }

    @Test
    fun testWithDataSet() {
        val secondDatasetRecord = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(\$a);",
            "Failed asserting that 1 is identical to 2.", null
        )
        writeTestStateWithDataset(secondDatasetRecord, "(2)")
        val thirdDatasetRecord = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(\$a);",
            "Failed asserting that 1 is identical to 3.", null
        )
        writeTestStateWithDataset(thirdDatasetRecord, "(3)")
        val methodRecord = TestStateStorage.Record(6, Date(), 0, -1, "", "", null)
        doTest(methodRecord)
    }

    @Test
    fun testWithNamedDataSet() {
        val secondDatasetRecord = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(\$a);",
            "Failed asserting that 1 is identical to 2.", null
        )
        writeTestStateWithDataset(secondDatasetRecord, "second dataset")
        val thirdDatasetRecord = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(\$a);",
            "Failed asserting that 1 is identical to 3.", null
        )
        writeTestStateWithDataset(thirdDatasetRecord, "third dataset")
        val methodRecord = TestStateStorage.Record(6, Date(), 0, -1, "", "", null)
        doTest(methodRecord)
    }

    @Test
    fun testWithDataSetAndSeveralFails() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(\$a);",
            "Failed asserting that 1 is identical to 2.", null
        )
        writeTestStateWithDataset(record, "\"dataset \"\"second dataset\"")

        val record2 = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(2)->toBe(\$a);",
            "Failed asserting that 2 is identical to 1.", null
        )
        writeTestStateWithDataset(record2, "\"dataset \"\"first dataset\"")
        val methodRecord = TestStateStorage.Record(6, Date(), 0, -1, "", "", null)
        doTest(methodRecord)
    }

    private fun checkHighlighting(): Unit = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            myFixture.checkHighlighting()
        }
    }

    private fun doWarningHighlighting(): List<HighlightInfo> = timeoutRunBlocking {
        withContext(Dispatchers.EDT) {
            myFixture.doHighlighting(HighlightSeverity.WARNING)
        }
    }
}

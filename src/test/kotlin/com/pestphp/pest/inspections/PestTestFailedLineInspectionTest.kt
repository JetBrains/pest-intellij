package com.pestphp.pest.inspections

import com.intellij.execution.TestStateStorage
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture
import java.util.*

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/inspections/pestTestFailedLine")
class PestTestFailedLineInspectionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/inspections/pestTestFailedLine"
    }

    override fun setUp() {
        super.setUp()

        myFixture.enableInspections(PestTestFailedLineInspection::class.java)
    }

    private fun writeTestState(record: TestStateStorage.Record) {
        TestStateStorage.getInstance(project).writeState("pest_qn://src/${getFileNameBeforeRelativePath()}::myTest", record)
    }

    private fun writeTestStateWithDataset(record: TestStateStorage.Record, dataset: String) {
        TestStateStorage.getInstance(project).writeState("pest_qn://src/${getFileNameBeforeRelativePath()}::myTest with data set $dataset", record)
    }

    private fun doTest(record: TestStateStorage.Record) {
        writeTestState(record)
        configureByFile()
        myFixture.checkHighlighting()
    }

    fun testFailedOneLine() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    fun testMismatchLine() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(3);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    fun testOutRange() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 12,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    fun testTypeBefore() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
        myFixture.type("\n echo 1;")
        val highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING)
        assertEquals(1, highlightInfos.size)
        assertEquals(76, highlightInfos[0].startOffset)
        assertEquals(95, highlightInfos[0].endOffset)
    }

    fun testTypeInside() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 5,
            "    expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
        myFixture.type("Not")
        val highlightInfos = myFixture.doHighlighting(HighlightSeverity.WARNING)
        assertEquals(1, highlightInfos.size)
        assertEquals(63, highlightInfos[0].startOffset)
        assertEquals(85, highlightInfos[0].endOffset)
    }

    fun testAnonymousFunction() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 7,
            "    });",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    fun testLambdaFunction() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 6,
            "    );",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    fun testSingleLeafElementReported() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 8,
            "    );",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

    fun testMultipleStatementsInOneLine() {
        val record = TestStateStorage.Record(
            6, Date(), 0, 4,
            "    expect(1)->toBe(1); expect(1)->toBe(2);",
            "Failed asserting that 1 is identical to 2.", null
        )
        doTest(record)
    }

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
}
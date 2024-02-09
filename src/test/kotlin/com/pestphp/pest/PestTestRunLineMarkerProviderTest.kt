package com.pestphp.pest

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.testFramework.TestDataPath

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/PestTestRunLineMarkerProviderTest")
class PestTestRunLineMarkerProviderTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestTestRunLineMarkerProviderTest"
    }

    private fun doTest(vararg expectedMarkerLines: Int) {
        myFixture.doHighlighting()
        val editor = myFixture.editor

        val markerList = DaemonCodeAnalyzerImpl.getLineMarkers(editor.document, project)
        val actualMarkerLines = markerList.map { marker -> editor.offsetToLogicalPosition(marker.startOffset).line }
        assertSameElements(expectedMarkerLines.toList(), actualMarkerLines)
    }

    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")
        doTest()
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")
        doTest(0, 5)
    }

    fun testFunctionCallNamedTestWithoutPest() {
        myFixture.configureByFile("FunctionCallNamedTestWithoutPest.php")
        doTest()
    }

    fun testAssignmentFunctionCallNamedTestWithoutPest() {
        myFixture.configureByFile("AssignmentFunctionCallNamedTestWithoutPest.php")
        doTest()
    }

    fun testAssignmentFunctionCallNamedTest() {
        myFixture.configureByFile("AssignmentFunctionCallNamedTest.php")
        doTest()
    }

    fun testFunctionCallNamedTestAsArgument() {
        myFixture.configureByFile("FunctionCallNamedTestAsArgument.php")
        doTest()
    }

    fun testFunctionCallNamedTestInsideDescribeBlock() {
        myFixture.configureByFile("FunctionCallNamedTestInsideDescribeBlock.php")
        doTest(0, 8, 9)
    }

    fun testFunctionCallNamedTestInsideTest() {
        myFixture.configureByFile("FunctionCallNamedTestInsideTest.php")
        doTest(0, 5)
    }
}

package com.pestphp.pest

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.configuration.PestRunConfigurationType

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/PestTestRunLineMarkerProviderTest")
class PestTestRunLineMarkerProviderTest : PestLightCodeFixture() {
    private lateinit var pestConfigurations: List<PhpTestFrameworkConfiguration>

    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestTestRunLineMarkerProviderTest"
    }

    override fun setUp() {
        super.setUp()
        pestConfigurations = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
    }

    override fun tearDown() {
        try {
            PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, pestConfigurations)
        } catch (e: Throwable) {
            addSuppressedException(e)
        } finally {
            super.tearDown()
        }
    }

    private fun doTest(vararg expectedMarkerLines: Int) {
        myFixture.doHighlighting()
        val editor = myFixture.editor

        val markerList = DaemonCodeAnalyzerImpl.getLineMarkers(editor.document, project)
        val actualMarkerLines = markerList.map { marker -> editor.offsetToLogicalPosition(marker.startOffset).line }
        assertSameElements(expectedMarkerLines.toList(), actualMarkerLines)
    }

    private fun initConfiguration() {
        val configuration = createPestFrameworkConfiguration()
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, listOf(configuration))
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

     fun testRunContextFromTestDirectory() {
         initConfiguration()
         val rootDirectory = myFixture.copyDirectoryToProject("contextProject", ".")
         val context = myFixture.psiManager.findDirectory(rootDirectory.findChild("tests")!!)!!
         val configurationsFromContext = ConfigurationContext(context).configurationsFromContext!!

         assertSize(2, configurationsFromContext)
     }

    fun testRunContextFromPestTestFile() {
        initConfiguration()
        myFixture.copyDirectoryToProject("contextProject", ".")
        val context = myFixture.configureByFile("contextProject/tests/Test.php")
        val configurationsFromContext = ConfigurationContext(context).configurationsFromContext!!

        assertSize(1, configurationsFromContext)
        assertEquals(PestRunConfigurationType.instance, configurationsFromContext[0].configurationType)
    }
}

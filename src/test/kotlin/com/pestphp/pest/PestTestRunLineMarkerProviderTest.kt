package com.pestphp.pest

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.TestDataPath
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.phpunit.PhpUnitLocalRunConfiguration
import com.jetbrains.php.phpunit.PhpUnitTestRunnerSettings
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.configuration.PestRunConfigurationType

@TestDataPath("\$CONTENT_ROOT/../resources/com/pestphp/pest/PestTestRunLineMarkerProviderTest")
class PestTestRunLineMarkerProviderTest : PestLightCodeFixture() {
    private lateinit var pestConfigurations: List<PhpTestFrameworkConfiguration>

    override fun getBasePath(): String = "${super.getBasePath()}/PestTestRunLineMarkerProviderTest"

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
        assertSameElements(actualMarkerLines, expectedMarkerLines.toList())
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
        doTest(0, 2)
    }

    fun testFunctionCallNamedItRedefinition() {
        myFixture.configureByFile("PestItFunctionCallWithRedefinition.php")
        doTest()
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
        doTest(0, 2, 3)
    }

    fun testFunctionCallNamedTestInsideTest() {
        myFixture.configureByFile("FunctionCallNamedTestInsideTest.php")
        doTest(0, 2)
    }

    fun testDataSetsAreNotYetMarkedAsRunnable() {
        myFixture.configureByFile("NamedDataSets.php")
        doTest(0, 2, 3, 9)
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

    fun testRunContextFromPhpUnitStyleFileOffersBothConfigs() {
        initConfiguration()
        myFixture.copyDirectoryToProject("contextProjectPhpUnit", ".")
        val context = myFixture.configureByFile("contextProjectPhpUnit/tests/PhpUnitStyleTest.php")
        val configurationsFromContext = ConfigurationContext(context).configurationsFromContext!!

        val types = configurationsFromContext.map { it.configurationType }.toSet()
        assertContainsElements(types, PestRunConfigurationType.instance)
        assertSize(2, configurationsFromContext)
    }

    fun testRunContextFromPhpUnitStyleMethodIsOwnedByPhpUnit() {
        initConfiguration()
        myFixture.copyDirectoryToProject("contextProjectPhpUnit", ".")
        val file = myFixture.configureByFile("contextProjectPhpUnit/tests/PhpUnitStyleTest.php")
        val method = PsiTreeUtil.findChildrenOfType(file, Method::class.java).single()
        val configurationsFromContext = ConfigurationContext(method).configurationsFromContext!!

        assertSize(1, configurationsFromContext)

        val phpUnitConfiguration = configurationsFromContext.single().configuration as PhpUnitLocalRunConfiguration
        assertEquals(
            "PHPUnit's own producer must own the individual method run",
            PhpUnitTestRunnerSettings.Scope.Method,
            phpUnitConfiguration.settings.testRunnerSettings.scope
        )
        assertTrue(
            "PHPUnit method-scope run must target the clicked method",
            phpUnitConfiguration.settings.testRunnerSettings.methodName?.endsWith("testFoo") == true
        )
    }
}

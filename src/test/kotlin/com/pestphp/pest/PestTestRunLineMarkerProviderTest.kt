package com.pestphp.pest

import com.intellij.codeInsight.daemon.impl.LineMarkersPass.getDisplayedLineMarkers
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.phpunit.PhpUnitLocalRunConfiguration
import com.jetbrains.php.phpunit.PhpUnitTestRunnerSettings
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.configuration.PestRunConfigurationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/PestTestRunLineMarkerProviderTest")
class PestTestRunLineMarkerProviderTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private val project get() = myFixture.project

    private lateinit var pestConfigurations: List<PhpTestFrameworkConfiguration>

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/PestTestRunLineMarkerProviderTest")
        pestConfigurations = PhpTestFrameworkSettingsManager.getInstance(project).getConfigurations(PestFrameworkType.instance)
    }

    @AfterEach
    fun tearDown() {
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, pestConfigurations)
    }

    private fun doTest(vararg expectedMarkerLines: Int) {
        myFixture.doHighlighting()
        val actualMarkerLines = timeoutRunBlocking {
            withContext(Dispatchers.EDT) {
                val editor = myFixture.editor

                val markerList = getDisplayedLineMarkers(editor.document, project)
                markerList.map { marker -> editor.offsetToLogicalPosition(marker.startOffset).line }
            }
        }
        assertEquals(expectedMarkerLines.size, actualMarkerLines.size, "$actualMarkerLines")
        assertEquals(expectedMarkerLines.toSet(), actualMarkerLines.toSet())
    }

    private fun initConfiguration() {
        val configuration = PestTestUtil.createPestFrameworkConfiguration(project)
        PhpTestFrameworkSettingsManager.getInstance(project).setConfigurations(PestFrameworkType.instance, listOf(configuration))
    }

    @Test
    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")
        doTest()
    }

    @Test
    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")
        doTest(0, 2)
    }

    @Test
    fun testFunctionCallNamedItRedefinition() {
        myFixture.configureByFile("PestItFunctionCallWithRedefinition.php")
        doTest()
    }

    @Test
    fun testFunctionCallNamedTestWithoutPest() {
        myFixture.configureByFile("FunctionCallNamedTestWithoutPest.php")
        doTest()
    }

    @Test
    fun testAssignmentFunctionCallNamedTestWithoutPest() {
        myFixture.configureByFile("AssignmentFunctionCallNamedTestWithoutPest.php")
        doTest()
    }

    @Test
    fun testAssignmentFunctionCallNamedTest() {
        myFixture.configureByFile("AssignmentFunctionCallNamedTest.php")
        doTest()
    }

    @Test
    fun testFunctionCallNamedTestAsArgument() {
        myFixture.configureByFile("FunctionCallNamedTestAsArgument.php")
        doTest()
    }

    @Test
    fun testFunctionCallNamedTestInsideDescribeBlock() {
        myFixture.configureByFile("FunctionCallNamedTestInsideDescribeBlock.php")
        doTest(0, 2, 3)
    }

    @Test
    fun testFunctionCallNamedTestInsideTest() {
        myFixture.configureByFile("FunctionCallNamedTestInsideTest.php")
        doTest(0, 2)
    }

    @Test
    fun testDataSetsAreNotYetMarkedAsRunnable() {
        myFixture.configureByFile("NamedDataSets.php")
        doTest(0, 2, 3, 9)
    }

    @Test
    fun testRunContextFromTestDirectory() {
        initConfiguration()
        val rootDirectory = myFixture.copyDirectoryToProject("contextProject", ".")
        val configurationsFromContext = runReadActionBlocking {
            val context = myFixture.psiManager.findDirectory(rootDirectory.findChild("tests")!!)!!
            ConfigurationContext(context).configurationsFromContext!!
        }

        assertEquals(2, configurationsFromContext.size)
    }

    @Test
    fun testRunContextFromPestTestFile() {
        initConfiguration()
        myFixture.copyDirectoryToProject("contextProject", ".")
        val context = myFixture.configureByFile("contextProject/tests/Test.php")
        val configurationsFromContext = runReadActionBlocking { ConfigurationContext(context).configurationsFromContext!! }

        assertEquals(1, configurationsFromContext.size)
        assertEquals(PestRunConfigurationType.instance, configurationsFromContext[0].configurationType)
    }

    @Test
    fun testRunContextFromPhpUnitStyleFileOffersBothConfigs() {
        initConfiguration()
        myFixture.copyDirectoryToProject("contextProjectPhpUnit", ".")
        val context = myFixture.configureByFile("contextProjectPhpUnit/tests/PhpUnitStyleTest.php")
        val configurationsFromContext = runReadActionBlocking { ConfigurationContext(context).configurationsFromContext!! }

        val types = configurationsFromContext.map { it.configurationType }.toSet()
        assertTrue(types.contains(PestRunConfigurationType.instance), "$types does not contain ${PestRunConfigurationType.instance}")
        assertEquals(2, configurationsFromContext.size)
    }

    @Test
    fun testRunContextFromPhpUnitStyleMethodIsOwnedByPhpUnit() {
        initConfiguration()
        myFixture.copyDirectoryToProject("contextProjectPhpUnit", ".")
        val file = myFixture.configureByFile("contextProjectPhpUnit/tests/PhpUnitStyleTest.php")
        val configurationsFromContext = runReadActionBlocking {
            val method = PsiTreeUtil.findChildrenOfType(file, Method::class.java).single()
            ConfigurationContext(method).configurationsFromContext!!
        }

        assertEquals(1, configurationsFromContext.size)

        val phpUnitConfiguration = configurationsFromContext.single().configuration as PhpUnitLocalRunConfiguration
        assertEquals(
            PhpUnitTestRunnerSettings.Scope.Method,
            phpUnitConfiguration.settings.testRunnerSettings.scope,
            "PHPUnit's own producer must own the individual method run"
        )
        assertTrue(
            phpUnitConfiguration.settings.testRunnerSettings.methodName?.endsWith("testFoo") == true,
            "PHPUnit method-scope run must target the clicked method"
        )
    }
}

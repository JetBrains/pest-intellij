package com.pestphp.pest.features.parallel

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.testframework.sm.runner.GeneralToSMTRunnerEventsConvertor
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsListener
import com.intellij.execution.testframework.sm.runner.SMTestProxy.SMRootTestProxy
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.util.Clock
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.TestDisposable
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.pestphp.pest.PestTestUtil
import com.pestphp.pest.configuration.PestRunConfiguration
import com.pestphp.pest.configuration.PestRunConfigurationProducer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@TestApplication
@TestDataPath($$"$PROJECT_ROOT/phpstorm/pest/src/test/resources/com/pestphp/pest/features/parallel")
class PestParallelSMTEventsAdapterTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    @TestDisposable
    lateinit var disposable: Disposable

    @BeforeEach
    fun setUp() {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/features/parallel")
    }

    @Test
    fun testSuitePresentableName() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///Users/me/my_project/vendor/pestphp/pest/src/Factories/TestCaseFactory.php(169) : eval()|'d code::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_foo' locationHint='php_qn:///Users/me/my_project/vendor/pestphp/pest/src/Factories/TestCaseFactory.php(169) : eval()|'d code::\P\ATest::__pest_evaluable_foo' flowId='6630']
##teamcity[testFailed name='__pest_evaluable_foo' message='Failed asserting that 1 is identical to 2.' details='/Users/me/my_project/tests/kek/MyThirdTest.php:4|n' duration='80' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_foo' duration='82' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']""".trimIndent()
        )
        val suite = testsRoot.children.first()
        assertEquals("P\\ATest", suite.name)
        assertEquals("ATest", suite.presentableName)
    }

    @Test
    fun testTestPresentableName() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///Users/me/my_project/vendor/pestphp/pest/src/Factories/TestCaseFactory.php(169) : eval()|'d code::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_foo' locationHint='php_qn:///Users/me/my_project/vendor/pestphp/pest/src/Factories/TestCaseFactory.php(169) : eval()|'d code::\P\ATest::__pest_evaluable_foo' flowId='6630']
##teamcity[testFailed name='__pest_evaluable_foo' message='Failed asserting that 1 is identical to 2.' details='/Users/me/my_project/tests/kek/MyThirdTest.php:4|n' duration='80' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_foo' duration='82' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']""".trimIndent()
        )
        val test = testsRoot.children.first().children.first()
        assertEquals("__pest_evaluable_foo", test.name)
        assertEquals("foo", test.presentableName)
    }

    @Test
    fun testArchTestPresentableName() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///Users/me/my_project/vendor/pestphp/pest/src/Factories/TestCaseFactory.php(169) : eval()|'d code::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_preset__→_php_' locationHint='php_qn:///Users/me/my_project/vendor/pestphp/pest/src/Factories/TestCaseFactory.php(169) : eval()|'d code::\P\ATest::__pest_evaluable_preset__→_php_' flowId='6630']
##teamcity[testFailed name='__pest_evaluable_preset__→_php_' message='Failed asserting that 1 is identical to 2.' details='/Users/me/my_project/tests/kek/MyThirdTest.php:4|n' duration='80' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_preset__→_php_' duration='82' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']""".trimIndent()
        )
        val test = testsRoot.children.first().children.first()
        assertEquals("__pest_evaluable_preset__→_php_", test.name)
        assertEquals("preset → php", test.presentableName)
    }

    @Test
    fun testDescribeDatasetSuiteAndChildrenUseRealNames() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[testCount count='2' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///src/a.php::\P\ATest' flowId='6630']
##teamcity[testSuiteStarted name='__pest_evaluable__my_block__→_check_valid' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable__my_block__→_check_valid' flowId='6630']
##teamcity[testStarted name='__pest_evaluable__my_block__→_check_valid_with_data_set_"(1)"' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable__my_block__→_check_valid_with_data_set_"(1)"' flowId='6630']
##teamcity[testFinished name='__pest_evaluable__my_block__→_check_valid_with_data_set_"(1)"' duration='1' flowId='6630']
##teamcity[testStarted name='__pest_evaluable__my_block__→_check_valid_with_data_set_"(2)"' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable__my_block__→_check_valid_with_data_set_"(2)"' flowId='6630']
##teamcity[testFinished name='__pest_evaluable__my_block__→_check_valid_with_data_set_"(2)"' duration='1' flowId='6630']
##teamcity[testSuiteFinished name='__pest_evaluable__my_block__→_check_valid' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']""".trimIndent()
        )
        val datasetSuite = testsRoot.children.first().children.first()
        assertEquals("my block → check valid", datasetSuite.presentableName)
        assertEquals(
            listOf("with data set \"(1)\"", "with data set \"(2)\""),
            datasetSuite.children.map { it.presentableName },
        )
    }

    @Test
    fun testTotalTimeDurationSeconds() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[enteredTheMatrix durationStrategy='MANUAL']
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///src/a.php::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_foo' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable_foo' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_foo' duration='2005' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']
Time: 00:02.072, Memory: 6.00 MB
OK (1 test, 1 assertion)""".trimIndent()
        )
        assertEquals(2072L, testsRoot.duration)
    }

    @Test
    fun testTotalTimeDurationMinutes() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[enteredTheMatrix durationStrategy='MANUAL']
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///src/a.php::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_foo' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable_foo' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_foo' duration='2005' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']
Time: 99:99.999, Memory: 99.99 Gb
OK (1 test, 1 assertion)""".trimIndent()
        )
        assertEquals(6039999L, testsRoot.duration)
    }

    @Test
    fun testWithoutTotalTimeDuration() {
        Clock.setTime(1000)
        try {
            val testsRoot = SMRootTestProxy()
            processTestOutput(
                testsRoot, """
##teamcity[enteredTheMatrix durationStrategy='MANUAL']
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///src/a.php::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_foo' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable_foo' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_foo' duration='30000' flowId='6630']""".trimIndent()
            )
            assertEquals(0L, testsRoot.duration)
        }
        finally {
            Clock.reset()
        }
    }

    @Test
    fun testAutomaticDurationStrategy() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///src/a.php::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_foo' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable_foo' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_foo' duration='2005' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']
Time: 99:99.999, Memory: 99.99 Gb
OK (1 test, 1 assertion)""".trimIndent()
        )
        assertEquals(2005L, testsRoot.duration)
    }

    @Test
    fun testInvalidDurationFormat() {
        Clock.setTime(1000)
        try {
            val testsRoot = SMRootTestProxy()
            processTestOutput(
                testsRoot, """
##teamcity[enteredTheMatrix durationStrategy='MANUAL']
##teamcity[testCount count='1' flowId='6630']
##teamcity[testSuiteStarted name='P\ATest' locationHint='php_qn:///src/a.php::\P\ATest' flowId='6630']
##teamcity[testStarted name='__pest_evaluable_foo' locationHint='php_qn:///src/a.php::\P\ATest::__pest_evaluable_foo' flowId='6630']
##teamcity[testFinished name='__pest_evaluable_foo' duration='2005' flowId='6630']
##teamcity[testSuiteFinished name='P\ATest' flowId='6630']
Time: 00:02Z072, Memory: 6.00 MB
OK (1 test, 1 assertion)""".trimIndent()
            )
            assertEquals(0L, testsRoot.duration)
        }
        finally {
            Clock.reset()
        }
    }

    private fun processTestOutput(testsRoot: SMRootTestProxy, output: String) {
        val file = myFixture.configureByFile("ATest.php")
        myFixture.project.messageBus.connect(disposable).subscribe(SMTRunnerEventsListener.TEST_STATUS, PestParallelSMTEventsAdapter())

        PestTestUtil.createPestFrameworkConfiguration(myFixture.project)
        val configuration = runReadActionBlocking {
            val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement<PsiElement>(file))
            PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration as? PestRunConfiguration
        }
        val consoleProperties = configuration?.createTestConsoleProperties(PestParallelTestExecutor()) ?: return
        Disposer.register(disposable, consoleProperties)
        val converter = OutputToGeneralTestEventsConverter("Pest", consoleProperties)
        converter.setProcessor(GeneralToSMTRunnerEventsConvertor(myFixture.project, testsRoot, "Pest"))
        StringUtil.splitByLinesKeepSeparators(output).forEach { line -> converter.process(line, ProcessOutputTypes.STDOUT) }
    }
}
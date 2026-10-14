package com.pestphp.pest.runner

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.testframework.sm.runner.GeneralToSMTRunnerEventsConvertor
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.execution.testframework.sm.runner.SMTestProxy.SMRootTestProxy
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.PsiElement
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
class PestSMTEventsAdapterTest {
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
    fun testDescribeBackticksStrippedFromPresentableName() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[testCount count='1' flowId='1']
##teamcity[testSuiteStarted name='Tests\Unit2\FirstTest' locationHint='php_qn:///src/FirstTest.php::\Tests\Unit2\FirstTest' flowId='1']
##teamcity[testStarted name='`my block` → is delicious' locationHint='php_qn:///src/FirstTest.php::\Tests\Unit2\FirstTest::`my block` → is delicious' flowId='1']
##teamcity[testFinished name='`my block` → is delicious' duration='1' flowId='1']
##teamcity[testSuiteFinished name='Tests\Unit2\FirstTest' flowId='1']""".trimIndent()
        )
        val test = testsRoot.children.first().children.first()
        assertEquals("`my block` → is delicious", test.name)
        assertEquals("my block → is delicious", test.presentableName)
    }

    @Test
    fun testDescribeBackticksStrippedFromDatasetSuiteAndChildren() {
        val testsRoot = SMRootTestProxy()
        processTestOutput(
            testsRoot, """
##teamcity[testCount count='2' flowId='1']
##teamcity[testSuiteStarted name='Tests\Unit2\FirstTest' locationHint='php_qn:///src/FirstTest.php::\Tests\Unit2\FirstTest' flowId='1']
##teamcity[testSuiteStarted name='`my block` → check valid' locationHint='php_qn:///src/FirstTest.php::\Tests\Unit2\FirstTest::`my block` → check valid' flowId='1']
##teamcity[testStarted name='`my block` → check valid with data set "(1)"' locationHint='php_qn:///src/FirstTest.php::\Tests\Unit2\FirstTest::`my block` → check valid with data set "(1)"' flowId='1']
##teamcity[testFinished name='`my block` → check valid with data set "(1)"' duration='1' flowId='1']
##teamcity[testStarted name='`my block` → check valid with data set "(2)"' locationHint='php_qn:///src/FirstTest.php::\Tests\Unit2\FirstTest::`my block` → check valid with data set "(2)"' flowId='1']
##teamcity[testFinished name='`my block` → check valid with data set "(2)"' duration='1' flowId='1']
##teamcity[testSuiteFinished name='`my block` → check valid' flowId='1']
##teamcity[testSuiteFinished name='Tests\Unit2\FirstTest' flowId='1']""".trimIndent()
        )
        val datasetSuite = testsRoot.children.first().children.first()
        assertEquals("`my block` → check valid", datasetSuite.name)
        assertEquals("my block → check valid", datasetSuite.presentableName)

        assertEquals(
            listOf("with data set \"(1)\"", "with data set \"(2)\""),
            datasetSuite.children.map { it.presentableName },
        )
    }

    private fun processTestOutput(testsRoot: SMRootTestProxy, output: String) {
        val file = myFixture.configureByFile("ATest.php")
        PestTestUtil.createPestFrameworkConfiguration(myFixture.project)
        val configuration = runReadActionBlocking {
            val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation.fromPsiElement<PsiElement>(file))
            PestRunConfigurationProducer().createConfigurationFromContext(context)?.configuration as? PestRunConfiguration
        }
        val consoleProperties = configuration?.createTestConsoleProperties(DefaultRunExecutor.getRunExecutorInstance()) ?: return
        Disposer.register(disposable, consoleProperties)
        val converter = OutputToGeneralTestEventsConverter("Pest", consoleProperties)
        converter.setProcessor(GeneralToSMTRunnerEventsConvertor(myFixture.project, testsRoot, "Pest"))
        StringUtil.splitByLinesKeepSeparators(output).forEach { line -> converter.process(line, ProcessOutputTypes.STDOUT) }
    }
}

package com.pestphp.pest.features.parallel

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.jetbrains.php.testFramework.run.PhpParallelTestSMTEventsAdapter
import com.pestphp.pest.PestTestId

class PestParallelSMTEventsAdapter : PhpParallelTestSMTEventsAdapter() {
    override fun onSuiteStarted(suite: SMTestProxy) {
        suite.setPresentableName(PestTestId.presentableOf(convertSuiteName(suite.name)))
        super.onSuiteStarted(suite)
    }

    override fun onTestStarted(test: SMTestProxy) {
        test.setPresentableName(PestTestId.presentableOf(convertRuntimeTestNameToRealTestName(test.presentableName)))
        super.onTestStarted(test)
    }
}

private fun convertSuiteName(suiteName: String): String =
    if (suiteName.startsWith("__pest_evaluable_")) {
        convertRuntimeTestNameToRealTestName(suiteName)
    } else {
        convertSuiteNameToClassName(suiteName)
    }

private const val PLACEHOLDER = " "

internal fun convertRuntimeTestNameToRealTestName(runtimeTestName: String): String =
    runtimeTestName
        .removePrefix("__pest_evaluable_")
        .replace("__→_", "  → ")
        .replace("__", PLACEHOLDER)
        .replace("_", " ")
        .replace(PLACEHOLDER, "_")

private fun convertSuiteNameToClassName(suiteName: String): String =
    suiteName.removePrefix("P\\")
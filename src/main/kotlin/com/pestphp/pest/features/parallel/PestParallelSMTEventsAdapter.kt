package com.pestphp.pest.features.parallel

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy

class PestParallelSMTEventsAdapter : SMTRunnerEventsAdapter() {
    override fun onSuiteStarted(suite: SMTestProxy) {
        suite.setPresentableName(convertSuiteNameToClassName(suite.name))
    }

    override fun onTestStarted(test: SMTestProxy) {
        test.setPresentableName(convertRuntimeTestNameToRealTestName(test.name))
    }
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
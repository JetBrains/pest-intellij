package com.pestphp.pest.features.parallel

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.jetbrains.php.testFramework.run.PhpParallelTestSMTEventsAdapter
import com.pestphp.pest.toPestTestPresentableName

class PestParallelSMTEventsAdapter : PhpParallelTestSMTEventsAdapter() {
    override fun onSuiteStarted(suite: SMTestProxy) {
        suite.setPresentableName(convertSuiteNameToClassName(suite.name).toPestTestPresentableName())
        super.onSuiteStarted(suite)
    }

    override fun onTestStarted(test: SMTestProxy) {
        test.setPresentableName(convertRuntimeTestNameToRealTestName(test.name).toPestTestPresentableName())
        super.onTestStarted(test)
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
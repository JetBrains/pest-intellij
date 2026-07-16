package com.pestphp.pest.runner

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.pestphp.pest.toPestTestPresentableName

class PestSMTEventsAdapter : SMTRunnerEventsAdapter() {
    override fun onTestStarted(test: SMTestProxy) {
        test.setPresentableName(test.presentableName.toPestTestPresentableName())
        super.onTestStarted(test)
    }

    override fun onSuiteStarted(suite: SMTestProxy) {
        suite.setPresentableName(suite.presentableName.toPestTestPresentableName())
        super.onSuiteStarted(suite)
    }
}

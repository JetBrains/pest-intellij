package com.pestphp.pest.runner

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.pestphp.pest.PestTestId

class PestSMTEventsAdapter : SMTRunnerEventsAdapter() {
    override fun onTestStarted(test: SMTestProxy) {
        test.setPresentableName(PestTestId.presentableOf(test.presentableName))
        super.onTestStarted(test)
    }

    override fun onSuiteStarted(suite: SMTestProxy) {
        suite.setPresentableName(PestTestId.presentableOf(suite.presentableName))
        super.onSuiteStarted(suite)
    }
}

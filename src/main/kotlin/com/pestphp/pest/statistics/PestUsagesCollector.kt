package com.pestphp.pest.statistics

import com.intellij.internal.statistic.eventLog.EventLogGroup
import com.intellij.internal.statistic.service.fus.collectors.CounterUsagesCollector
import com.intellij.openapi.project.Project

object PestUsagesCollector : CounterUsagesCollector() {
    private val GROUP = EventLogGroup("pest", 2)
    private val PEST_MUTATION_TEST_EXECUTED = GROUP.registerEvent("pest.mutation.test.executed")
    private val PEST_PARALLEL_TEST_EXECUTED = GROUP.registerEvent("pest.parallel.test.executed")

    override fun getGroup(): EventLogGroup = GROUP

    fun logMutationTestExecution(project: Project) {
        PEST_MUTATION_TEST_EXECUTED.log(project)
    }

    fun logParallelTestExecution(project: Project) {
        PEST_PARALLEL_TEST_EXECUTED.log(project)
    }
}
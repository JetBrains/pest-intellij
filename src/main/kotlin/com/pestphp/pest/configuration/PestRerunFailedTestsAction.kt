package com.pestphp.pest.configuration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentContainer
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.getPestTestName

class PestRerunFailedTestsAction(
    componentContainer: ComponentContainer,
    properties: SMTRunnerConsoleProperties?
) : AbstractRerunFailedTestsAction(componentContainer) {
    override fun getRunProfile(environment: ExecutionEnvironment): MyRunProfile? {
        val profile = myConsoleProperties.configuration

        if (profile !is PestRunConfiguration) {
            return null
        }

        val runConfiguration: PestRunConfiguration = profile
        return object : MyRunProfile(runConfiguration) {
            override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
                val runConfiguration: PestRunConfiguration = this.peer as PestRunConfiguration
                val project: Project = runConfiguration.project
                val interpreter: PhpInterpreter = runConfiguration.interpreter
                if (
                    PhpTestFrameworkSettingsManager.getInstance(project).getConfigByInterpreter(
                        PestFrameworkType.getInstance(),
                        interpreter
                    ) == null
                ) {
                    return null
                }

                val failed = this@PestRerunFailedTestsAction.getFailedTests(project)
                    .filter { it.isLeaf }
                    .filter { it.parent != null }
                    .map { it.getLocation(project, GlobalSearchScope.allScope(project)) }
                    .mapNotNull { it.psiElement.getPestTestName() }

                val clone: PestRunConfiguration = runConfiguration.clone() as PestRunConfiguration

                clone.settings.runnerSettings.directoryPath = null
                clone.settings.runnerSettings.filePath = null
                val command: PhpCommandSettings = clone.createCommand(
                    interpreter,
                    mapOf(),
                    listOf(),
                    false
                )

                command.addArgument(
                    "--filter=/${failed.reduce { result, testName -> result + '|' + testName.replace(" ", "\\s") }}$/"
                )

                return runConfiguration.getState(
                    environment,
                    command,
                    null as ProcessListener?
                )
            }
        }
    }

    init {
        init(properties)
    }
}

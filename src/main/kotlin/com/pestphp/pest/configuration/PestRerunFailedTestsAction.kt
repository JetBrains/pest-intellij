package com.pestphp.pest.configuration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.openapi.ui.ComponentContainer
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.isPestTestReference

class PestRerunFailedTestsAction(
    componentContainer: ComponentContainer,
    properties: SMTRunnerConsoleProperties
) : AbstractRerunFailedTestsAction(componentContainer) {
    override fun getRunProfile(environment: ExecutionEnvironment): MyRunProfile? {
        val profile = myConsoleProperties.configuration

        if (profile !is PestRunConfiguration) {
            return null
        }

        val runConfiguration: PestRunConfiguration = profile
        return object : MyRunProfile(runConfiguration) {
            override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
                val peerRunConfiguration = this.peer as PestRunConfiguration
                val project = peerRunConfiguration.project
                val interpreter = peerRunConfiguration.interpreter
                if (
                    PhpTestFrameworkSettingsManager.getInstance(project).getConfigByInterpreter(
                        PestFrameworkType.instance,
                        interpreter
                    ) == null
                ) {
                    return null
                }

                val failed = getFailedTests(project)
                    .asSequence()
                    .filter { it.isLeaf }
                    .filter { it.parent != null }
                    .map { it.getLocation(project, GlobalSearchScope.allScope(project)) }
                    .mapNotNull { it?.psiElement }
                    .mapNotNull {
                        if (it.isPestTestReference()) {
                            return@mapNotNull it.getPestTestName()
                        } else {
                            return@mapNotNull null
                        }
                        // TODO: add condition for phpunit test
                    }
                    .toList()

                val clone: PestRunConfiguration = peerRunConfiguration.clone() as PestRunConfiguration

                // If there are no failed tests found, it's prob.
                // because it's an pest version before the new printer
                if (failed.isEmpty()) {
                    return peerRunConfiguration.getState(
                        environment,
                        clone.createCommand(
                            interpreter,
                            mapOf(),
                            listOf(),
                            false
                        ),
                        null
                    )
                }

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

                return peerRunConfiguration.getState(
                    environment,
                    command,
                    null
                )
            }
        }
    }

    init {
        init(properties)
    }
}

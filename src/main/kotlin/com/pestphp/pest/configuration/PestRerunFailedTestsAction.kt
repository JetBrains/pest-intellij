package com.pestphp.pest.configuration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.openapi.ui.ComponentContainer
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.composer.lib.ComposerLibraryManager
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.pestphp.pest.PestBundle
import com.pestphp.pest.PestFrameworkType
import com.pestphp.pest.isPestTestReference
import com.pestphp.pest.notifications.OutdatedNotification
import com.pestphp.pest.toPestTestRegex

/**
 * Adds support for rerunning failed tests
 */
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
                val config = PhpTestFrameworkSettingsManager.getInstance(project).getConfigByInterpreter(
                    PestFrameworkType.instance,
                    interpreter
                )
                if (config == null) {
                    return null
                }

                val failed = getFailedTests(project)
                    .asSequence()
                    .filter { it.isLeaf }
                    .filter { it.parent != null }
                    .map { it.getLocation(project, GlobalSearchScope.allScope(project)) }
                    .mapNotNull { it?.psiElement }
                    .filter { it.isPestTestReference() }
                    .toList()

                val clone: PestRunConfiguration = peerRunConfiguration.clone() as PestRunConfiguration

                // If there are no failed tests found, it's prob.
                // because it's an pest version before the new printer
                if (failed.isEmpty()) {
                    OutdatedNotification().notify(
                        project,
                        PestBundle.message("NO_FAILED_TESTS_FOUND")
                    )

                    return peerRunConfiguration.getState(
                        environment,
                        clone.createCommand(
                            interpreter,
                            mutableMapOf(),
                            mutableListOf(),
                            false
                        ),
                        null
                    )
                }

                val command: PhpCommandSettings = clone.createCommand(
                    interpreter,
                    mutableMapOf(),
                    mutableListOf(),
                    false
                )

                val rootPath =
                    ComposerLibraryManager.getInstance(project).findVendorDirForUpsource()?.parent?.path
                        ?: command.workingDirectory

                val testcases = failed.mapNotNull { it.toPestTestRegex(rootPath) }
                    .reduce { result, testName -> "$result|$testName" }

                command.addArgument(
                    "--filter=/$testcases/"
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

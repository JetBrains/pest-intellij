package com.pestphp.pest.configuration

import com.intellij.execution.ExecutionException
import com.intellij.openapi.project.Project
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.testFramework.run.PhpTestRunConfigurationHandler
import com.pestphp.pest.PestBundle
import com.pestphp.pest.toPestTestRegex

class PestRunConfigurationHandler : PhpTestRunConfigurationHandler {
    var rootPath: String? = null

    companion object {
        @JvmField
        val instance = PestRunConfigurationHandler()
    }

    override fun getConfigFileOption(): String {
        return "--configuration"
    }

    override fun prepareCommand(project: Project, commandSettings: PhpCommandSettings, exe: String, version: String?) {
        commandSettings.setScript(exe, false)
        commandSettings.addArgument("--teamcity")
        commandSettings.addEnv("IDE_PEST_EXE", exe)

        if (!version.isNullOrEmpty()) {
            commandSettings.addEnv("IDE_PEST_VERSION", version)
        }
    }

    @Throws(ExecutionException::class)
    override fun runType(
        project: Project,
        phpCommandSettings: PhpCommandSettings,
        type: String,
        workingDirectory: String
    ) {
        throw ExecutionException(PestBundle.message("CANNOT_RUN_PEST_WITH_TYPE_MESSAGE"))
    }

    override fun runDirectory(
        project: Project,
        phpCommandSettings: PhpCommandSettings,
        directory: String,
        workingDirectory: String
    ) {
        if (directory.isEmpty()) {
            return
        }
        phpCommandSettings.addPathArgument(directory)
    }

    override fun runFile(
        project: Project,
        phpCommandSettings: PhpCommandSettings,
        file: String,
        workingDirectory: String
    ) {
        if (file.isEmpty()) {
            return
        }
        phpCommandSettings.addPathArgument(file)
    }

    override fun runMethod(
        project: Project,
        phpCommandSettings: PhpCommandSettings,
        file: String,
        methodName: String,
        workingDirectory: String
    ) {
        if (file.isEmpty()) {
            return
        }
        val pathMapper = phpCommandSettings.pathProcessor.createPathMapper(project)

        val rootPath = this.rootPath ?: workingDirectory

        phpCommandSettings.addPathArgument(file)
        phpCommandSettings.addArgument("--filter")
        phpCommandSettings.addArgument("/${methodName.toPestTestRegex(rootPath, file, pathMapper)}/")
    }
}

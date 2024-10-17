package com.pestphp.pest.configuration

import com.intellij.execution.ExecutionException
import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpTestFrameworkVersionDetector
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.pestphp.pest.PestBundle
import org.jetbrains.annotations.Nls

private val VERSION_REGEX = Regex("(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)")
private val VERSION_OPTIONS = arrayOf("--version", "--colors=never")

class PestVersionDetector : PhpTestFrameworkVersionDetector<String>() {
    override fun getPresentableName(): @Nls String {
        return PestBundle.message("FRAMEWORK_NAME")
    }

    override fun getTitle(): String {
        return PestBundle.message("GETTING_PEST_VERSION")
    }

    override fun getVersionOptions(): Array<String> {
        return VERSION_OPTIONS
    }

    public override fun parse(s: String): String {
        val version = if (s.startsWith("Pest")) {
            // for <2.0.0 versions
            s.removePrefix("Pest").substringBefore("\n").trim()
        } else {
            // for 2.* versions
            s.trim().removePrefix("Pest Testing Framework ").substringBeforeLast('.')
        }

        if (!version.matches(VERSION_REGEX)) {
            throw ExecutionException(PestBundle.message("PEST_CONFIGURATION_UI_CAN_NOT_PARSE_VERSION", s))
        }
        return version
    }

    override fun getVersion(project: Project, interpreter: PhpInterpreter, executable: String?): String {
        if (interpreter.isRemote) {
            throw ExecutionException(PestBundle.message("PEST_VERSION_IS_NOT_SUPPORTED_FOR_REMOTE_INTERPRETER"))
        }
        return super.getVersion(project, interpreter, executable)
    }

    companion object {
        val instance = PestVersionDetector()
    }
}

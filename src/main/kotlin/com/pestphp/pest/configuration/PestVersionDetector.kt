package com.pestphp.pest.configuration

import com.intellij.execution.ExecutionException
import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpTestFrameworkVersionDetector
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkVersionCache
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

    companion object {
        val instance = PestVersionDetector()
    }

    fun getVersionWithCache(
        project: Project,
        interpreter: PhpInterpreter,
        config: PhpTestFrameworkConfiguration,
        executable: String?
    ): String {
        val cached = PhpTestFrameworkVersionCache.getCache(project, config)

        if (cached.isNotBlank()) {
            return cached
        }

        return super.getVersion(project, interpreter, executable)
    }

    fun getCachedVersion(project: Project, config: PhpTestFrameworkConfiguration): String? {
        val cached = PhpTestFrameworkVersionCache.getCache(project, config)

        if (cached.isNotBlank()) {
            return cached
        }

        return null
    }
}

package com.pestphp.pest.configuration

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpTestFrameworkVersionDetector
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration
import com.jetbrains.php.testFramework.PhpTestFrameworkVersionCache
import com.pestphp.pest.PestBundle
import org.jetbrains.annotations.Nls

class PestVersionDetector : PhpTestFrameworkVersionDetector<String>() {
    override fun getPresentableName(): @Nls String {
        return PestBundle.message("FRAMEWORK_NAME")
    }

    override fun parse(s: String): String {
        return s.removePrefix("Pest").substringBefore("\n").trim()
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
}

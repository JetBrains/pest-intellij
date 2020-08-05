package com.pestphp.pest.configuration

import com.intellij.openapi.diagnostic.Logger
import com.jetbrains.php.PhpTestFrameworkVersionDetector
import com.pestphp.pest.PestBundle
import org.jetbrains.annotations.Nls

class PestVersionDetector : PhpTestFrameworkVersionDetector<String>() {
    override fun getPresentableName(): @Nls String {
        return PestBundle.message("FRAMEWORK_NAME")
    }

    override fun parse(s: String): String {
        LOG.info(String.format("Parsing version: %s", s))
        return s
    }

    companion object {
        private val LOG = Logger.getInstance(PestVersionDetector::class.java)
        val instance = PestVersionDetector()
    }
}

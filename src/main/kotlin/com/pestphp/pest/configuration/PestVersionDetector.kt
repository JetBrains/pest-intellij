package com.pestphp.pest.configuration

import com.jetbrains.php.PhpTestFrameworkVersionDetector
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
}

package com.pestphp.pest.configuration

import com.jetbrains.php.testFramework.run.PhpTestDebugRunner

class PestDebugRunner private constructor() : PhpTestDebugRunner<PestRunConfiguration>(PestRunConfiguration::class.java) {
    override fun getRunnerId(): String {
        return "PestDebugRunner"
    }
}
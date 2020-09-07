package com.pestphp.pest.parser

import com.jetbrains.php.lang.psi.resolve.types.PhpType

data class PestConfigurationFile(
    val baseTestType: PhpType,
    val pathsClasses: List<Pair<String, PhpType>>
)

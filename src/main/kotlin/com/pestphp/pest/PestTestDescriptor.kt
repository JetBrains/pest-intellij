package com.pestphp.pest

import com.intellij.util.SmartList
import com.jetbrains.php.phpunit.PhpUnitTestDescriptor
import com.jetbrains.php.testFramework.PhpTestCreateInfo

object PestTestDescriptor : PhpUnitTestDescriptor() {
    override fun getTestCreateInfos(): MutableCollection<PhpTestCreateInfo> {
        return SmartList(PestTestCreateInfo)
    }
}
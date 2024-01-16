package com.pestphp.pest

import com.intellij.util.SmartList
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.phpunit.PhpUnitTestDescriptor
import com.jetbrains.php.testFramework.PhpTestCreateInfo
import java.util.*

/**
 * findTests, findClasses, and findMethods return empty collections,
 * since Pest tests are function calls, not methods, and therefore are not located in PHP classes
 */
object PestTestDescriptor : PhpUnitTestDescriptor() {
    override fun findTests(clazz: PhpClass): MutableCollection<PhpClass> {
        return Collections.emptySet()
    }

    override fun findTests(method: Method): MutableCollection<Method> {
        return Collections.emptySet()
    }

    override fun findClasses(test: PhpClass, testName: String): MutableCollection<PhpClass> {
        return Collections.emptySet()
    }

    override fun findMethods(testMethod: Method): MutableCollection<Method> {
        return Collections.emptySet()
    }

    override fun getTestCreateInfos(): MutableCollection<PhpTestCreateInfo> {
        return SmartList(PestTestCreateInfo)
    }
}
package com.pestphp.pest.types

import com.intellij.testFramework.TestDataPath
import com.pestphp.pest.PestLightCodeFixture

@TestDataPath("\$CONTENT_ROOT/resources/com/pestphp/pest/types")
abstract class BaseTypeTestCase : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("TestCase.php")
    }

    override fun getTestDataPath(): String {
        return "$basePath/types"
    }
}

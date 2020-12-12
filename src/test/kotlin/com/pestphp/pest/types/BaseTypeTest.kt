package com.pestphp.pest.types

import com.pestphp.pest.PestLightCodeFixture

@Suppress("UnnecessaryAbstractClass")
abstract class BaseTypeTest : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("TestCase.php")
    }

    override fun getTestDataPath(): String {
        return "$basePath/types"
    }
}

package com.pestphp.pest.tests.types

import com.pestphp.pest.tests.PestLightCodeFixture

abstract class BaseTypeTest : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("TestCase.php")
    }

    override fun getTestDataPath(): String? {
        return basePath + "types/fixtures"
    }
}

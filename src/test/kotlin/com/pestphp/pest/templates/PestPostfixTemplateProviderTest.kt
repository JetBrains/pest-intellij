package com.pestphp.pest.templates

import com.pestphp.pest.PestLightCodeFixture

class PestPostfixTemplateProviderTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/templates"
    }

    private fun doTest() {
        myFixture.configureByFile(getTestName(true) + ".php")
        myFixture.type('\t')
        myFixture.checkResultByFile(getTestName(true) + ".after.php", true)
    }

    fun testIt() {
        doTest()
    }

    fun testDescribe() {
        doTest()
    }
}
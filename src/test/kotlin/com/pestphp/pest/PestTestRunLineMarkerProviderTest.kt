package com.pestphp.pest

class PestTestRunLineMarkerProviderTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String? {
        return "src/test/resources/com/pestphp/pest/PestTestRunLineMarkerProviderTest"
    }

    fun testMethodCallNamedItAndVariableTestIsNotPestTest() {
        val file = myFixture.configureByFile("MethodCallNamedItAndVariableTest.php")

        val testElement = file.firstChild.lastChild.firstChild.firstChild

        assertNull(PestTestRunLineMarkerProvider().getInfo(testElement))
    }

    fun testFunctionCallNamedItWithDescriptionAndClosure() {
        val file = myFixture.configureByFile("PestItFunctionCallWithDescriptionAndClosure.php")

        val testElement = file.firstChild.lastChild.firstChild.firstChild

        assertNotNull(PestTestRunLineMarkerProvider().getInfo(testElement))
    }
}

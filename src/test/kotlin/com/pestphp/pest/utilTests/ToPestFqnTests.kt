package com.pestphp.pest.utilTests

import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.toPestFqn

class ToPestFqnTests : PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/PestUtil"

    fun testCanGeneratePqn() {
        val file = myFixture.configureByFile(
            "PestItFunctionCallWithDescriptionAndClosure.php",
        )

        val testElement = file?.firstChild?.lastChild?.firstChild

        val pqn = testElement?.toPestFqn()

        assertTrue(pqn?.any { it.contains("::") } == true)
        assertTrue(pqn?.any { it.contains("pest_qn:") } == true)
    }
}

package com.pestphp.pest.pestUtil

import com.intellij.psi.PsiManager
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.toPestFqn

class ToPestFqnTests : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/PestUtil"
    }

    fun testCanGeneratePqn() {
        val virtualFile = myFixture.copyFileToProject(
            "PestItFunctionCallWithDescriptionAndClosure.php",
            "tests/Unit/PestTest.php"
        )
        val file = PsiManager.getInstance(project).findFile(virtualFile)

        val testElement = file?.firstChild?.lastChild?.firstChild

        val pqn = testElement?.toPestFqn()

        assertTrue(pqn?.any { it.contains("::") } == true)
        assertTrue(pqn?.any { it.contains("pest_qn:") } == true)
    }
}

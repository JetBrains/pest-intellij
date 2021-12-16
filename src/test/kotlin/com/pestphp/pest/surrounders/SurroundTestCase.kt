package com.pestphp.pest.surrounders

import com.intellij.codeInsight.generation.surroundWith.SurroundWithHandler
import com.intellij.lang.surroundWith.Surrounder
import com.pestphp.pest.PestLightCodeFixture

abstract class SurroundTestCase: PestLightCodeFixture() {
    protected fun doTest(surrounder: Surrounder, textBefore: String, textAfter: String) {
        myFixture.configureByText("simpleTest.php", textBefore)
        SurroundWithHandler.invoke(project, myFixture.editor, myFixture.file, surrounder)
        myFixture.checkResult(textAfter)
    }
}
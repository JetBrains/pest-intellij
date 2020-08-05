package com.pestphp.pest.tests

import com.intellij.openapi.util.Iconable.ICON_FLAG_VISIBILITY
import com.pestphp.pest.PestIconProvider
import com.pestphp.pest.PestIcons
import junit.framework.TestCase
import kotlin.test.assertNotEquals

class PestIconProviderTest : PestLightCodeFixture() {
    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("SimpleTest.php")
    }

    override fun getTestDataPath(): String? {
        return basePath + "fixtures"
    }

    fun testCanGetPestIconForPestFile() {
        val file = myFixture.configureByFile("SimpleTest.php")

        TestCase.assertEquals(
            PestIconProvider().getIcon(file, ICON_FLAG_VISIBILITY),
            PestIcons.FILE
        )
    }

    fun testCanGetOtherIconForNonPestFile() {
        val file = myFixture.configureByFile("SimpleScript.php")

        assertNotEquals(
            PestIcons.FILE,
            PestIconProvider().getIcon(file, ICON_FLAG_VISIBILITY)
        )
    }
}

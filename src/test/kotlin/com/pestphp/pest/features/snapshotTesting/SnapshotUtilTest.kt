package com.pestphp.pest.features.snapshotTesting

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture
import junit.framework.TestCase

class SnapshotUtilTest : PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/snapshotTesting"

    fun testIsSnapshotAssertion() {
        val file = myFixture.configureByFile("allSnapshotAssertions.php")

        val references = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
        assertNotEmpty(references)

        references.forEach { TestCase.assertTrue(it.isSnapshotAssertionCall) }
    }

    fun testIsNotSnapshotAssertion() {
        val file = myFixture.configureByFile("nonSnapshotAssertions.php")

        val functionReference = PsiTreeUtil.findChildOfType(file, FunctionReferenceImpl::class.java)!!

        assertFalse(functionReference.isSnapshotAssertionCall)
    }

    fun testCanFindSnapshotFiles() {
        myFixture.copyFileToProject(
            "tests/__snapshots__/snapshotTest__it_renders_correctly__1.txt",
            "tests/__snapshots__/snapshotTest__it_renders_correctly__1.txt"
        )

        val file = myFixture.configureByFile("snapshotTest.php")

        val snapshotReference = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
            .first { it.isSnapshotAssertionCall }

        assertSize(1, snapshotReference.snapshotFiles)
    }
}
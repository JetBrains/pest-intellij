package com.pestphp.pest.snapshotTesting

import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture
import junit.framework.TestCase
import org.junit.Ignore
import org.junit.Test

class SnapshotUtilTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/snapshotTesting"
    }

    fun testIsSnapshotAssertion() {
        val file = myFixture.configureByFile("allSnapshotAssertions.php")

        val statements = file.firstChild.children
            .map { it.firstChild }
        assertNotEmpty(statements)

        statements.forEach { assertInstanceOf(it, FunctionReferenceImpl::class.java) }

        statements
            .filterIsInstance<FunctionReferenceImpl>()
            .forEach {
                TestCase.assertTrue(it.isSnapshotAssertionCall)
            }
    }

    fun testIsNotSnapshotAssertion() {
        val file = myFixture.configureByFile("nonSnapshotAssertions.php")

        val functionReference = file.firstChild.children[0].firstChild as FunctionReferenceImpl

        assertFalse(functionReference.isSnapshotAssertionCall)
    }

    @Ignore
    @Test
    fun canFindSnapshotFiles() {
        val file = myFixture.configureByFile("snapshotTest.php")

        val pestTest = file.firstChild.children[0].firstChild as FunctionReferenceImpl

        val pestTestBody = pestTest.parameters[1].firstChild as FunctionImpl

        val snapshotReference = pestTestBody.children[1].children[1].firstChild as FunctionReferenceImpl

        assertSize(1, snapshotReference.snapshotFiles)
    }
}
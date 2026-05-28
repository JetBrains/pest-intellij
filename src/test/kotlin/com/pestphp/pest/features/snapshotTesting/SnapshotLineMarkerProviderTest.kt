package com.pestphp.pest.features.snapshotTesting

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture

class SnapshotLineMarkerProviderTest: PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/snapshotTesting"

    fun testCanProvideIconForSnapshotAssertion() {
        val file = myFixture.configureByFile("allSnapshotAssertions.php")

        val snapshotCalls = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java)
            .filter { it.isSnapshotAssertionCall }
        assertNotEmpty(snapshotCalls)

        assertSize(snapshotCalls.count(), this.myFixture.findAllGutters())
    }

    fun testDoNotShowIconForSnapshotUse() {
        myFixture.configureByFile("snapshotAssertionUseStatement.php")

        assertSize(0, this.myFixture.findAllGutters())
    }
}
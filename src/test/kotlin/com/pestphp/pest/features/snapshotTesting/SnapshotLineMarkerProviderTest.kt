package com.pestphp.pest.features.snapshotTesting

import com.pestphp.pest.PestLightCodeFixture

class SnapshotLineMarkerProviderTest: PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/snapshotTesting"
    }

    fun testCanProvideIconForSnapshotAssertion() {
        val file = myFixture.configureByFile("allSnapshotAssertions.php")

        val identifiers = file.firstChild.children
            .map { it.firstChild.firstChild }
        assertNotEmpty(identifiers)

        assertSize(identifiers.count(), this.myFixture.findAllGutters())
    }

    fun testDoNotShowIconForSnapshotUse() {
        myFixture.configureByFile("snapshotAssertionUseStatement.php")

        assertSize(0, this.myFixture.findAllGutters())
    }
}
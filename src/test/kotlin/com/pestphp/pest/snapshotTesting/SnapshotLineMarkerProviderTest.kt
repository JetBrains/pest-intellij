package com.pestphp.pest.snapshotTesting

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
}
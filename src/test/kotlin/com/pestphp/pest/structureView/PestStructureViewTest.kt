package com.pestphp.pest.structureView

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewTreeElement
import com.jetbrains.php.lang.psi.PhpFile
import com.pestphp.pest.PestIcons
import com.pestphp.pest.PestLightCodeFixture
import javax.swing.Icon

class PestStructureViewTest : PestLightCodeFixture() {
    override fun getBasePath(): String = "${super.getBasePath()}/structureView"

    private data class Node(val name: String?, val icon: Icon?, val children: List<Node>)

    private fun describe(name: String, vararg children: Node) = Node(name, AllIcons.Nodes.TestGroup, children.toList())
    private fun test(name: String) = Node(name, PestIcons.Logo, emptyList())

    private fun StructureViewTreeElement.toNode(): Node = presentation.let { presentation ->
        Node(
            presentation.presentableText,
            presentation.getIcon(false),
            children.filterIsInstance<StructureViewTreeElement>().map { it.toNode() },
        )
    }

    private fun topLevelNodes(fileName: String): List<Node> {
        val file = myFixture.configureByFile(fileName) as PhpFile
        return PestStructureViewExtension().getChildren(file)
            .map { it.toNode() }
    }

    fun testDescribeBlockExposesNestedTestsAsChildren() {
        val roots = topLevelNodes("DescribeBlockWithNestedTests.php")

        assertEquals(
            listOf(
                describe(
                    "my block",
                    test("is delicious"),
                    test("check valid"),
                ),
                test("top level test"),
            ),
            roots,
        )
    }

    fun testNestedDescribeBlocksAreNestedInTree() {
        val roots = topLevelNodes("NestedDescribeBlocks.php")

        assertEquals(
            listOf(
                describe(
                    "SomeClass",
                    test("it is visible as nested"),
                    describe(
                        "SomeMethod",
                        test("it is visible as twice nested"),
                    ),
                ),
            ),
            roots,
        )
    }
}

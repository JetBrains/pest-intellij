package com.pestphp.pest.configuration

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.PlatformTestUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.phpunit.PhpPsiLocationWithDataSet
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.PestLightCodeFixture

class PestLocationProviderTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "$basePath/configuration/locationProvider"
    }

    fun testSubprojectFor2xVersion() {
        val testName = "test"
        doTestGetLocation(
            "Test.php::$testName", testName, "subdir"
        )
    }

    fun testSubprojectFor1xVersion() {
        val testName = "test"
        doTestGetLocation(
            "Test.php::$testName", testName, "subdir", true
        )
    }

    private fun doTestGetLocation(
        pathSuffix: String,
        expectedTestName: String,
        configurationFileRelativePath: String? = null,
        isAbsolutePath: Boolean = false,
    ) {
        val testDir = PlatformTestUtil.getTestName(name, false)
        myFixture.copyDirectoryToProject(testDir, ".")
        val configurationFileRootPath = "${myFixture.testDataPath}/${testDir}${configurationFileRelativePath?.let { "/$it" } ?: ""}"
        val locationProvider = PestLocationProvider(PhpPathMapper.create(project), project, configurationFileRootPath)
        val path = if (isAbsolutePath) "$configurationFileRootPath/$pathSuffix" else pathSuffix
        val resolvedLocation = locationProvider
            .getLocation("pest_qn", path, project, GlobalSearchScope.allScope(project)).firstOrNull()

        assertInstanceOf(resolvedLocation, PhpPsiLocationWithDataSet::class.java)
        assertInstanceOf(resolvedLocation?.psiElement, FunctionReference::class.java)
        assertEquals((resolvedLocation?.getPsiElement() as? FunctionReference)?.name, expectedTestName)
    }
}
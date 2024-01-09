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

    fun testSubproject() {
        val testName = "test"
        doTestGetLocation(
            "Test.php::$testName", testName, "subdir"
        )
    }

    private fun doTestGetLocation(
        pathSuffix: String,
        expectedTestName: String,
        configurationFileRelativePath: String? = null,
    ) {
        val testDir = PlatformTestUtil.getTestName(name, false)
        myFixture.copyDirectoryToProject(testDir, ".")
        val configurationFileRootPath = "${myFixture.testDataPath}/${testDir}${configurationFileRelativePath?.let { "/$it" } ?: ""}"
        val locationProvider = PestLocationProvider(PhpPathMapper.create(project), project, configurationFileRootPath)
        val resolvedLocation = locationProvider
            .getLocation("pest_qn", pathSuffix, project, GlobalSearchScope.allScope(project)).firstOrNull()

        assertInstanceOf(resolvedLocation, PhpPsiLocationWithDataSet::class.java)
        assertInstanceOf(resolvedLocation?.psiElement, FunctionReference::class.java)
        assertEquals((resolvedLocation?.getPsiElement() as? FunctionReference)?.name, expectedTestName)
    }
}
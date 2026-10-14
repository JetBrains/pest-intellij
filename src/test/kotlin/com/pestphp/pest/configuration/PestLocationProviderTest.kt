package com.pestphp.pest.configuration

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.platform.testFramework.junit5.codeInsight.fixture.codeInsightModuleFixture
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.junit5.TestApplication
import com.intellij.testFramework.junit5.fixture.projectFixture
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.phpunit.PhpPsiLocationWithDataSet
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.PestTestUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

@TestApplication
class PestLocationProviderTest {
    companion object {
        private val projectFixture = projectFixture(openAfterCreation = true)
    }

    private val myFixture by codeInsightModuleFixture(projectFixture)

    private lateinit var testMethodName: String

    @BeforeEach
    fun setUp(testInfo: TestInfo) {
        PestTestUtil.setUp(myFixture, "${PestTestUtil.getTestDataPath()}/configuration/locationProvider")
        testMethodName = testInfo.testMethod.get().name
    }

    @Test
    fun testSubprojectFor2xVersion() {
        val testName = "test"
        doTestGetLocation(
            "Test.php::$testName", testName, "subdir"
        )
    }

    @Test
    fun testSubprojectFor1xVersion() {
        val testName = "test"
        doTestGetLocation(
            "Test.php::$testName", testName, "subdir", true
        )
    }

    @Test
    fun testDescribeBlock() {
        doTestGetLocation(
            "Test.php::`sum` → check true", "test", "subdir"
        )
    }

    @Test
    fun testDescribeBlockIt() {
        doTestGetLocation(
            "Test.php::`sum` → it check valid", "it", "subdir"
        )
    }

    private fun doTestGetLocation(
        pathSuffix: String,
        expectedTestName: String,
        configurationFileRelativePath: String? = null,
        isAbsolutePath: Boolean = false,
    ) {
        val testDir = PlatformTestUtil.getTestName(testMethodName, false)
        myFixture.copyDirectoryToProject(testDir, ".")
        val configurationFileRootPath = "${myFixture.testDataPath}/${testDir}${configurationFileRelativePath?.let { "/$it" } ?: ""}"
        val project = myFixture.project
        val locationProvider = PestLocationProvider(PhpPathMapper.create(project), project, configurationFileRootPath)
        val path = if (isAbsolutePath) "$configurationFileRootPath/$pathSuffix" else pathSuffix
        val (resolvedLocation, psiElement, testName) = runReadActionBlocking {
            val resolvedLocation = locationProvider
                .getLocation("pest_qn", path, project, GlobalSearchScope.allScope(project)).firstOrNull()
            Triple(resolvedLocation, resolvedLocation?.psiElement, (resolvedLocation?.getPsiElement() as? FunctionReference)?.name)
        }

        assertInstanceOf(PhpPsiLocationWithDataSet::class.java, resolvedLocation)
        assertInstanceOf(FunctionReference::class.java, psiElement)
        assertEquals(testName, expectedTestName)
    }
}
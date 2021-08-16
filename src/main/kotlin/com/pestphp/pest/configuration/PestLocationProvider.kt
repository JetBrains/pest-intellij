package com.pestphp.pest.configuration

import com.intellij.execution.Location
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.config.commandLine.PhpCommandLinePathProcessor
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.phpunit.PhpPsiLocationWithDataSet
import com.jetbrains.php.phpunit.PhpUnitQualifiedNameLocationProvider
import com.jetbrains.php.util.pathmapper.PhpLocalPathMapper
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import com.pestphp.pest.runner.LocationInfo

/**
 * Adds support for goto test from test results.
 */
class PestLocationProvider(project: Project) : SMTestLocator {
    private val protocolId = "pest_qn"
    private val phpUnitLocationProvider = PhpUnitQualifiedNameLocationProvider.create(
        PhpCommandLinePathProcessor.LOCAL.createPathMapper(project)
    )

    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        scope: GlobalSearchScope
    ): MutableList<Location<PsiElement>> {
        if (protocol != protocolId) {
            return phpUnitLocationProvider.getLocation(protocol, path, project, scope)
        }

        val locationInfo = getLocationInfo(path, project)
        val element = locationInfo?.let { findElement(it, project) } ?: return mutableListOf()

        return mutableListOf(
            PhpPsiLocationWithDataSet(
                project,
                element,
                getDataSet(locationInfo)
            )
        )
    }

    private fun getDataSet(locationInfo: LocationInfo): String? {
        return locationInfo.testName
    }

    private fun getLocationInfo(link: String, project: Project): LocationInfo? {
        val location = link.split("::")

        val file = PhpLocalPathMapper(project).getLocalFile(location[0])

        if (location.size == 1) {
            return file?.let { LocationInfo(it, null) }
        }

        val testName = location[1]
        return file?.let { LocationInfo(it, testName) }
    }

    private fun findElement(locationInfo: LocationInfo, project: Project): PsiElement? {
        return this.getLocation(
            project,
            locationInfo.file,
            locationInfo.testName
        )
    }

    private fun getLocation(project: Project, virtualFile: VirtualFile, testName: String?): PsiElement? {
        val file = PsiManager.getInstance(project).findFile(virtualFile) ?: return null

        if (testName == null) {
            return file
        }

        return (file as PhpFile).getPestTests().firstOrNull { it.getPestTestName() == testName }
    }
}

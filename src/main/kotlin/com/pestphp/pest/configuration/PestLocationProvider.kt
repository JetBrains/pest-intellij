package com.pestphp.pest.configuration

import com.intellij.execution.Location
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.phpunit.PhpPsiLocationWithDataSet
import com.jetbrains.php.phpunit.PhpUnitQualifiedNameLocationProvider
import com.jetbrains.php.util.pathmapper.PhpPathMapper
import com.pestphp.pest.getPestTestName
import com.pestphp.pest.getPestTests
import com.pestphp.pest.runner.LocationInfo

/**
 * Adds support for goto test from test results.
 */
class PestLocationProvider(
    private val pathMapper: PhpPathMapper,
    private val project: Project,
    private val configurationFileRootPath: String? = null
) : SMTestLocator {
    private val protocolId = "pest_qn"
    private val phpUnitLocationProvider = PhpUnitQualifiedNameLocationProvider.create(pathMapper)

    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        scope: GlobalSearchScope
    ): MutableList<Location<PsiElement>> {
        if (protocol != protocolId) {
            return phpUnitLocationProvider.getLocation(protocol, path, project, scope)
        }

        val locationInfo = getLocationInfo(path)
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

    private fun getLocationInfo(link: String): LocationInfo? {
        val location = link.split("::")

        val pathPrefix = configurationFileRootPath ?: project.basePath
        val fileUrl = if (pathPrefix != null && location[0].startsWith(pathPrefix)) {
            location[0] // for Pest versions 1.x
        } else {
            "$pathPrefix/${location[0]}" // for Pest versions >= 2.x
        }
        val file = this.pathMapper.getLocalFile(fileUrl)

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

    override fun getLocation(
        stacktraceLine: String,
        project: Project,
        scope: GlobalSearchScope
    ): MutableList<Location<PsiElement>> {
        return mutableListOf()
    }
}

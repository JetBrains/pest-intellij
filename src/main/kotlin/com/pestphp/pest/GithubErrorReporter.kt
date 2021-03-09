package com.pestphp.pest

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.util.Consumer
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager
import com.jetbrains.php.testFramework.PhpTestFrameworkVersionCache
import java.awt.Component
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Generates a issue creation link
 * https://github.com/pestphp/pest-intellij/issues/new?title=foo&body=bar
 */
class GithubErrorReporter : ErrorReportSubmitter() {
    companion object {
        private const val URL = "https://github.com/pestphp/pest-intellij/issues/new?title="
        private const val ENCODING = "UTF-8"
        private const val STACKTRACE_LENGTH = 3000
    }

    override fun getReportActionText(): String {
        return "Report to Pest Issue Tracker"
    }

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val event = events.firstOrNull()
        val title = event?.throwableText?.lineSequence()?.first()
            ?: event?.message
            ?: "Crash Report: <Fill in title>"
        val body = event?.throwableText ?: "Please paste the full stacktrace from the IDEA error popup."
        val version = PluginManagerCore.getPlugin(PluginId.getId("com.pestphp.pest-intellij"))?.version

        val project: Project = ProjectManager.getInstance().openProjects.first()
        val pestVersion = getPestVersion(project)
        val intelliJVersion = ApplicationInfo.getInstance().build.asString()

        val builder = StringBuilder(URL)
        try {
            builder.append(URLEncoder.encode(title, ENCODING))
            builder.append("&body=")
            builder.append(
                URLEncoder.encode(
                    """
                    || Q                | A
                    || ---------------- | -----
                    || Bug report?      | yes
                    || Plugin version   | $version
                    || Pest version     | $pestVersion
                    || IntelliJ version | $intelliJVersion
                    || OS               | ${System.getProperty("os.name")}
                    |
                    |### Description
                    |${additionalInfo ?: ""}
                    |
                    |### Stacktrace
                    |```
                    |${body.take(STACKTRACE_LENGTH)}
                    |```
                    """.trimMargin(),
                    ENCODING
                )
            )
        } catch (e: UnsupportedEncodingException) {
            consumer.consume(
                SubmittedReportInfo(
                    null,
                    null,
                    SubmittedReportInfo.SubmissionStatus.FAILED
                )
            )
            return false
        }

        BrowserUtil.browse(builder.toString())
        consumer.consume(
            SubmittedReportInfo(
                null,
                "GitHub issue",
                SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
            )
        )
        return true
    }

    private fun getPestVersion(project: Project?): String {
        return PhpTestFrameworkSettingsManager.getInstance(project)
            .getLocalConfig(PestFrameworkType.instance)?.let {
                PhpTestFrameworkVersionCache.getCache(project, it)
            } ?: "x.y.z"
    }
}

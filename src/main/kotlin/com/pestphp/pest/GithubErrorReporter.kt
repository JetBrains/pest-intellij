package com.pestphp.pest

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.util.Consumer
import java.awt.Component

/**
 * Generates an issue creation link
 * https://github.com/pestphp/pest-intellij/issues/new?title=foo&body=bar
 */
class GithubErrorReporter : ErrorReportSubmitter() {
    companion object {
        private const val URL = "https://github.com/pestphp/pest-intellij/issues"
    }

    override fun getReportActionText(): String {
        return "Report to Pest Issue Tracker"
    }

    @Suppress("SwallowedException")
    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        BrowserUtil.browse(URL)
        consumer.consume(
            SubmittedReportInfo(
                URL,
                "GitHub issue",
                SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
            )
        )
        return true
    }
}

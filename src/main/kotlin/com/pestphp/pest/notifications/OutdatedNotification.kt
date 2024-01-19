package com.pestphp.pest.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls

class OutdatedNotification {
    private val group = NotificationGroupManager.getInstance()
        .getNotificationGroup("Outdated Pest")

    fun notify(project: Project?, @Nls content: String): Notification {
        val notification: Notification = group.createNotification(content, NotificationType.ERROR)
        notification.notify(project)
        return notification
    }
}

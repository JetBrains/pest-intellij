package com.pestphp.pest.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class OutdatedNotification {
    private val group = NotificationGroupManager.getInstance()
        .getNotificationGroup("Outdated Pest")

    fun notify(content: String): Notification {
        return notify(null, content)
    }

    fun notify(project: Project?, content: String): Notification {
        val notification: Notification = group.createNotification(content, NotificationType.ERROR)
        notification.notify(project)
        return notification
    }
}

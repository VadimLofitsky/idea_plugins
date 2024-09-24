package lofitsky.idea_plugins.plugin.util

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager

class PluginUtil {
    companion object {
        fun notify(
            message: Any?,
            notificationType: NotificationType = NotificationType.INFORMATION,
            groupDisplayId: String = "groupDisplayId",
        ): Unit {
            val msg = message?.toString()?.takeIf { it.isNotEmpty() } ?: return
            val notification = Notification(groupDisplayId, msg, notificationType)
            ApplicationManager.getApplication().messageBus.syncPublisher(Notifications.TOPIC).notify(notification)
        }
    }
}

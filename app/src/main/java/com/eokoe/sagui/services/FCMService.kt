package com.eokoe.sagui.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.data.net.services.SaguiService
import com.eokoe.sagui.extensions.fromString
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsActivity
import com.eokoe.sagui.utils.DATE_FORMAT
import com.eokoe.sagui.utils.LogUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        LogUtil.startCrashlytics(this)
        if (remoteMessage.data != null && remoteMessage.data.isNotEmpty()) {
            val id = remoteMessage.data.getValue("id")
            val event = remoteMessage.data.getValue("event")
            val message = remoteMessage.data.getValue("message")
            val type = remoteMessage.data.getValue("type")
            val createdAt = remoteMessage.data.getValue("create_at")
            val date = Date().fromString(DATE_FORMAT, createdAt)

            val notification = Notification(eventStr = event, typeStr = type, resourceId = id.toLowerCase(), message = message, createdAt = date)
            saveNotification(notification)
        }
    }

    private fun saveNotification(notification: Notification) {
        SaguiModelImpl(this, ServiceGenerator.getService(SaguiService::class.java))
                .saveNotification(notification)
                .subscribe({
                    showNotification(notification)
                }, {
                    LogUtil.error(this, it)
                })
    }

    private fun showNotification(notification: Notification) {
        val intent = ComplaintDetailsActivity.getIntent(
                this, notification.resourceId, notification.id!!, true)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, notification.eventStr)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(notification.message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(arrayOf(1000L, 500L, 1000L, 500L, 1000L).toLongArray())
                .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }
}
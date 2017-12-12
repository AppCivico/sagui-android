package com.eokoe.sagui.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.extensions.fromString
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsActivity
import com.eokoe.sagui.utils.DATE_FORMAT
import com.eokoe.sagui.utils.LogUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import java.util.*


/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class FCMService : FirebaseMessagingService() {
    private val saguiModel by inject<SaguiModel>()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        LogUtil.startCrashlytics(this)
        if (remoteMessage.data?.isNotEmpty() == true) {
            remoteMessage.data.run {
                val createdAt = Date().fromString(DATE_FORMAT, getValue("create_at"))
                val notification = Notification(
                        eventStr = getValue("event"),
                        typeStr = getValue("type"),
                        resourceId = getValue("id").toLowerCase(),
                        message = getValue("message"),
                        createdAt = createdAt
                )
                saveNotification(notification)
            }
        }
    }

    private fun saveNotification(notification: Notification) {
        saguiModel.saveNotification(notification)
                .subscribe(
                        { showNotification(notification) },
                        { LogUtil.error(this, it) }
                )
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

    companion object {
        val TAG = FCMService::class.simpleName!!
    }
}
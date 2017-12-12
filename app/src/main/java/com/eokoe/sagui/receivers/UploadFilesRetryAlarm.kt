package com.eokoe.sagui.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eokoe.sagui.services.upload.UploadFilesJobIntentService
import com.eokoe.sagui.utils.Job

/**
 * @author Pedro Silva
 * @since 03/10/17
 */
class UploadFilesRetryAlarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        UploadFilesJobIntentService.enqueueWork(context)
    }

    companion object {
        fun startAlarm(context: Context) {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmMgr.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (1000 * 60 * 60),
                    getPendingIntent(context))
        }

        fun cancelAlarm(context: Context) {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmMgr.cancel(getPendingIntent(context))
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, UploadFilesRetryAlarm::class.java)
            return PendingIntent.getBroadcast(context, Job.UPLOAD_FILES_RETRY, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}
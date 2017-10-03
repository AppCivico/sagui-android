package com.eokoe.sagui.services.upload_file

import android.content.Context
import com.eokoe.sagui.receivers.UploadFilesRetryAlarm
import com.eokoe.sagui.services.Retry

/**
 * @author Pedro Silva
 * @since 03/10/17
 */
class UploadFilesRetryDefault : Retry {
    override fun schedule(context: Context) {
        UploadFilesRetryAlarm.startAlarm(context)
    }

    override fun cancel(context: Context) {
        UploadFilesRetryAlarm.cancelAlarm(context)
    }
}
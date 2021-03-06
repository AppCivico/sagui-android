package com.eokoe.sagui.services.upload

import android.content.Context
import com.eokoe.sagui.receivers.UploadFilesRetryAlarm

/**
 * @author Pedro Silva
 * @since 03/10/17
 */
class UploadFilesRetryDefault : UploadFilesRetry {
    override fun schedule(context: Context) {
        UploadFilesRetryAlarm.startAlarm(context)
    }

    override fun cancel(context: Context) {
        UploadFilesRetryAlarm.cancelAlarm(context)
    }
}
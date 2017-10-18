package com.eokoe.sagui.services.upload_file

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.JobIntentService
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.services.Retry
import com.eokoe.sagui.utils.Job
import com.eokoe.sagui.utils.LogUtil

/**
 * @author Pedro Silva
 * @since 03/10/17
 */
class UploadFilesJobIntentService : JobIntentService() {

    private lateinit var saguiModel: SaguiModel
    private lateinit var retry: Retry

    override fun onCreate() {
        super.onCreate()
        saguiModel = SaguiModelImpl(applicationContext)
        retry = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UploadFilesRetryLollipop()
        } else {
            UploadFilesRetryDefault()
        }
    }

    override fun onHandleWork(intent: Intent) {
        saguiModel.getAssetsPendingUpload()
                .flatMapIterable { it }
                .flatMap { saguiModel.sendAsset(it) }
                .filter { !it.sent }
                .count()
                .subscribe({ count ->
                    val call =
                            if (count > 0) retry::schedule
                            else retry::cancel
                    call(applicationContext)
                }, { err ->
                    LogUtil.error(this, err)
                })
    }

    companion object {
        fun enqueueWork(context: Context) {
            enqueueWork(context, UploadFilesJobIntentService::class.java, Job.UPLOAD_FILES, Intent(context, UploadFilesJobIntentService::class.java))
        }
    }
}
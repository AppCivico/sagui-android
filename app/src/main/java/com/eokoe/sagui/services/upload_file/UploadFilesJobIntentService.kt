package com.eokoe.sagui.services.upload_file

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.JobIntentService
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.services.Retry
import com.eokoe.sagui.utils.Job

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
                .flatMap { asset ->
                    saguiModel.sendAsset(asset)
                }
                .filter { !it.sent }
                .count()
                .subscribe({ count ->
                    if (count > 0) {
                        retry.schedule(applicationContext)
                    } else {
                        retry.cancel(applicationContext)
                    }
                }, { err ->
                    err.printStackTrace()
                })
    }

    companion object {
        fun enqueueWork(context: Context) {
            enqueueWork(context, UploadFilesJobIntentService::class.java, Job.UPLOAD_FILES, Intent(context, UploadFilesJobIntentService::class.java))
        }
    }
}
package com.eokoe.sagui.services.upload

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.utils.Job
import com.eokoe.sagui.utils.LogUtil
import org.koin.android.ext.android.inject
import org.koin.standalone.releaseContext

/**
 * @author Pedro Silva
 * @since 03/10/17
 */
class UploadFilesJobIntentService : JobIntentService() {

    private val saguiModel by inject<SaguiModel>()
    private val retry by inject<UploadFilesRetry>()

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
                }, { err -> LogUtil.error(this, err) })
    }

    override fun onDestroy() {
        releaseContext(TAG)
        super.onDestroy()
    }

    companion object {
        val TAG = UploadFilesJobIntentService::class.simpleName!!

        fun enqueueWork(context: Context) {
            enqueueWork(context, UploadFilesJobIntentService::class.java, Job.UPLOAD_FILES,
                    Intent(context, UploadFilesJobIntentService::class.java))
        }
    }
}
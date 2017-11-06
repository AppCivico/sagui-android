package com.eokoe.sagui.services

import android.annotation.TargetApi
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import com.eokoe.sagui.services.upload.UploadFilesJobIntentService
import com.eokoe.sagui.utils.Job

/**
 * @author Pedro Silva
 * @since 03/10/17
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class JobSchedulerService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        when (params.jobId) {
            Job.UPLOAD_FILES_RETRY -> retryUploadFiles()
        }
        return false
    }

    private fun retryUploadFiles() {
        UploadFilesJobIntentService.enqueueWork(applicationContext)
    }

    override fun onStopJob(params: JobParameters) = false
}
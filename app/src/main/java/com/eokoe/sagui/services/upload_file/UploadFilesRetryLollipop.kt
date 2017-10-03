package com.eokoe.sagui.services.upload_file

import android.annotation.TargetApi
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.eokoe.sagui.services.JobSchedulerService
import com.eokoe.sagui.services.Retry
import com.eokoe.sagui.utils.Job

/**
 * @author Pedro Silva
 * @since 03/10/17
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class UploadFilesRetryLollipop : Retry {
    override fun schedule(context: Context) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder = JobInfo.Builder(Job.UPLOAD_FILES_RETRY, ComponentName(context.packageName, JobSchedulerService::class.simpleName))
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
        jobScheduler.schedule(builder.build())
    }

    override fun cancel(context: Context) {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(Job.UPLOAD_FILES_RETRY)
    }
}
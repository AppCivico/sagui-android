package com.eokoe.sagui.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.eokoe.sagui.extensions.toFile
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * @author Pedro Silva
 * @since 07/08/17
 */
class UploadFileService : IntentService(UploadFileService::class.java.name) {

    override fun onHandleIntent(intent: Intent) {
        val formKey = intent.getStringExtra(EXTRA_KEY_DATA)
        val fileUri = intent.getParcelableExtra<Uri>(EXTRA_FILE_DATA)
        val file = fileUri.toFile(this)
        if (file != null && file.exists()) {
            val requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(fileUri)), file)
            val formData = MultipartBody.Part.createFormData(formKey, file.name, requestFile)
            send(formData)
        }
    }

    fun send(body: MultipartBody.Part) {

    }

    companion object {
        private val JOB_ID = 1
        private val EXTRA_KEY_DATA = "EXTRA_KEY_DATA"
        private val EXTRA_FILE_DATA = "EXTRA_FILE_DATA"

        fun getIntent(context: Context, key: String, file: Uri): Intent {
            val intent = Intent(context, UploadFileService::class.java)
            intent.putExtra(EXTRA_KEY_DATA, key)
            intent.putExtra(EXTRA_FILE_DATA, file)
            return intent
        }
    }
}

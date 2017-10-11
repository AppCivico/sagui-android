package com.eokoe.sagui.features.complaints.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Confirmation
import com.eokoe.sagui.data.entities.ContributeOptions
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.*
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.ComplaintsActivity
import com.eokoe.sagui.features.show_asset.ShowAssetActivity
import com.eokoe.sagui.services.upload_file.UploadFilesJobIntentService
import com.eokoe.sagui.utils.FileUtil
import com.eokoe.sagui.utils.Files
import com.eokoe.sagui.utils.RequestCode
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_complaint_details.*
import java.io.File

/**
 * @author Pedro Silva
 * @since 28/09/17
 */
class ComplaintDetailsActivity : BaseActivity(),
        ConfirmContract.View, ViewPresenter<ConfirmContract.Presenter> {

    override lateinit var presenter: ConfirmContract.Presenter

    private var complaint: Complaint? = null
    private var complaintId: String? = null
    private var confirmation = Confirmation()
    private lateinit var loadingDialog: LoadingDialog
    private var isConfirmed: Boolean = false
    private var notificationId: String? = null
    private var isFromNotification: Boolean = false
    private var fileAttached: File? = null
    private var openPreview: Boolean = false
    private var updateConfirmation: Boolean = false
    private lateinit var detailsAdapter: ComplaintDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_details)
    }

    override fun onResume() {
        super.onResume()
        if (openPreview) {
            openPreview = false
            openPreview()
        }
        if (updateConfirmation) {
            updateConfirmation = false
            presenter.updateConfirmation(confirmation)
        }
    }

    override fun onBackPressed() {
        if (isFromNotification && complaint?.enterprise != null && complaint?.category != null) {
            val intent = ComplaintsActivity.getIntent(this, complaint?.enterprise!!, complaint?.category!!, true)
            startActivity(intent)
            finish()
            return
        }
        if (isConfirmed) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        if (complaint == null) {
            complaint = intent.extras.getParcelable(EXTRA_COMPLAINT)
        }
        isFromNotification = intent.extras.getBoolean(EXTRA_IS_FROM_NOTIFICATION)
        complaintId = intent.extras.getString(EXTRA_COMPLAINT_ID)
        notificationId = intent.extras.getString(EXTRA_NOTIFICATION_ID)
        presenter = ConfirmPresenter(SaguiModelImpl())
        loadingDialog = LoadingDialog.newInstance(getString(R.string.loading_confirm_complaint))
        btnConfirm.setOnClickListener {
            //openContributeDialog()
            getConfirmDialog().show(supportFragmentManager)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        rvComplaintDetails.setHasFixedSize(true)
        detailsAdapter = ComplaintDetailsAdapter(complaint)
        detailsAdapter.onImageClickListener = object : AssetsAdapter.OnItemClickListener {
            override fun onItemClick(asset: Asset) {
                val intent = ShowAssetActivity.getIntent(this@ComplaintDetailsActivity, asset)
                startActivity(intent)
            }
        }
        rvComplaintDetails.adapter = detailsAdapter
        if (complaint != null) {
            onLoadComplaint(complaint!!)
        } else {
            presenter.getComplaint(complaintId!!)
        }
        if (notificationId != null) {
            presenter.markAsRead(notificationId!!)
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notifications, menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_notifications -> {
                AlertDialogFragment
                        .create(this) {
                            title = "Notificações"
                            message = "Deseja receber notificações sobre a reclamação?"
                            positiveText = "Sim"
                            negativeText = "Não"
                            onConfirmClickListener{ dialog, _ ->
                                FirebaseMessaging.getInstance().subscribeToTopic("complaint-${complaint!!.id}")
                                dialog.dismiss()
                            }
                            onCancelClickListener { dialog, _ ->
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("complaint-${complaint!!.id}")
                                dialog.dismiss()
                            }
                        }
                        .show(supportFragmentManager)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onComplaintConfirmed(confirmation: Confirmation) {
        isConfirmed = true
        this.confirmation = confirmation
        getContributeDialog().show(supportFragmentManager)
    }

    override fun showLoading() {
        loadingDialog.show(supportFragmentManager)
    }

    override fun hideLoading() {
        loadingDialog.dismiss()
    }

    override fun showError(error: Throwable) {
        hideLoading()
        getErrorDialog(error).show(supportFragmentManager)
    }

    private fun getConfirmDialog(): AlertDialogFragment {
        return AlertDialogFragment.create(this) {
            titleRes = R.string.confirm_complaint
            messageRes = R.string.confirm_complaint_question
            positiveTextRes = R.string.confirm
            negativeTextRes = R.string.cancel
            cancelable = true
            onConfirmClickListener { _, _ ->
                presenter.confirmComplaint(confirmation)
            }
        }
    }

    private fun getContributeDialog(): AlertDialogFragment {
        return AlertDialogFragment.create(this) {
            titleRes = R.string.confirmed
            messageRes = R.string.msg_contribute
            positiveTextRes = R.string.contribute
            negativeTextRes = R.string.no
            cancelable = true
            onConfirmClickListener { dialog, _ ->
                openContributeDialog()
                dialog.dismiss()
            }
        }
    }

    private fun getContributeSuccess(): AlertDialogFragment {
        return AlertDialogFragment.create(this) {
            titleRes = R.string.congratulations
            messageRes = R.string.successful_contribution
            cancelable = true
        }
    }

    private fun openContributeDialog() {
        getAlertList(ContributeOptions.list(this)) { dialog, position ->
            when (ContributeOptions.fromPosition(position)) {
                ContributeOptions.TAKE_PICTURE -> if (hasCameraPermission()) {
                    takePicture()
                } else {
                    requestCameraPermission(RequestCode.Permission.CAMERA_PICTURE.value)
                }
                ContributeOptions.GALLERY_PICTURE -> openImageGallery()
                ContributeOptions.RECORD_VIDEO -> if (hasCameraPermission()) {
                    recordVideo()
                } else {
                    requestCameraPermission(RequestCode.Permission.CAMERA_VIDEO.value)
                }
                ContributeOptions.GALLERY_VIDEO -> openVideoGallery()
                ContributeOptions.AUDIO -> openAudioGallery()
            }
            dialog.dismiss()
        }.show()
    }

    private fun openImageGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.GALLERY_PICTURE.value)
        }
    }

    private fun openVideoGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("video/*")
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.GALLERY_VIDEO.value)
        }
    }

    private fun openAudioGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("audio/*")
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.AUDIO.value)
        }
    }

    private fun takePicture() {
        fileAttached = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                generateFilename(Files.Extensions.JPG)
        )
        val file = fileAttached!!.getUri(this)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
        grantUriRwPermissions(intent, file)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.CAMERA_PICTURE.value)
        }
    }

    private fun recordVideo() {
        fileAttached = File(
                getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                generateFilename(Files.Extensions.MP4)
        )
        val file = fileAttached!!.getUri(this)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
        grantUriRwPermissions(intent, file)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.CAMERA_VIDEO.value)
        }
    }

    private fun getErrorDialog(error: Throwable): AlertDialogFragment {
        return AlertDialogFragment.create(this) {
            titleRes = R.string.error
            message = when (error.errorType) {
                ErrorType.CONNECTION -> "Error de conexão.\nPor favor verifique sua internet e tente novamente"
                ErrorType.CUSTOM -> error.friendlyMessage
                else -> "Ocorreu um erro inexperado.\nTente novamente mais tarde"
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (RequestCode.Intent.fromInt(requestCode)) {
            RequestCode.Intent.CAMERA_PICTURE -> if (resultCode == Activity.RESULT_OK && fileAttached?.exists() == true) {
                val imagePath = File(filesDir, Files.Path.IMAGE_PATH)
                val privateFile = File(
                        imagePath,
                        fileAttached!!.name
                )
                val inputFilePath = Uri.fromFile(fileAttached).getRealPath(this)!!
                FileUtil.compressImage(inputFilePath, privateFile)
                val uriImage = data?.data ?: Uri.fromFile(fileAttached)
                try {
                    fileAttached?.delete()
                    contentResolver.delete(uriImage, null, null)
                } catch (error: Exception) {
                    error.printStackTrace()
                }
                confirmation.files.add(Asset(localPath = privateFile.path, type = "image/*"))
                openPreview = true
            }

            RequestCode.Intent.GALLERY_PICTURE -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val imagePath = File(filesDir, Files.Path.IMAGE_PATH)
                val privateFile = File(
                        imagePath,
                        generateFilename(Files.Extensions.JPG)
                )
                val tempFile = File.createTempFile(getString(R.string.app_name) + "_confirmation_", ".jpg")
                uri.copyTo(this, tempFile)
                FileUtil.compressImage(tempFile.path, privateFile)
                confirmation.files.add(Asset(localPath = privateFile.path, type = "image/*"))
                openPreview = true
            }

            RequestCode.Intent.CAMERA_VIDEO -> if (resultCode == Activity.RESULT_OK && fileAttached?.exists() == true) {
                val videoPath = File(filesDir, Files.Path.VIDEO_PATH)
                val privateFile = File(
                        videoPath,
                        fileAttached!!.name
                )
                val uriVideo = data?.data ?: Uri.fromFile(fileAttached)
                fileAttached!!.copyTo(privateFile, true)
                try {
                    fileAttached?.delete()
                    contentResolver.delete(uriVideo, null, null)
                } catch (error: Exception) {
                    error.printStackTrace()
                }
                confirmation.files.add(Asset(localPath = privateFile.path, type = "video/*"))
                openPreview = true
            }

            RequestCode.Intent.GALLERY_VIDEO -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val imagePath = File(filesDir, Files.Path.VIDEO_PATH)
                val privateFile = File(
                        imagePath,
                        generateFilename(Files.Extensions.MP4)
                )
                uri.copyTo(this, privateFile)
                confirmation.files.add(Asset(localPath = privateFile.path, type = "video/*"))
                openPreview = true
            }
            RequestCode.Intent.AUDIO -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val audioPath = File(filesDir, Files.Path.AUDIO_PATH)
                val privateFile = File(
                        audioPath,
                        generateFilename(Files.Extensions.NONE)
                )
                uri.copyTo(this, privateFile)
                confirmation.files.add(Asset(localPath = privateFile.path, type = "audio/*"))
                openPreview = true
            }
            RequestCode.Intent.PREVIEW_ASSET -> if (resultCode == Activity.RESULT_OK) {
                updateConfirmation = true
            } else {
                confirmation.files.clear()
            }
        }
    }

    private fun openPreview() {
        val intent = ShowAssetActivity.getIntent(this, confirmation.files, showSendButton = true)
        startActivityForResult(intent, RequestCode.Intent.PREVIEW_ASSET.value)
    }

    private fun generateFilename(extension: String): String {
        return resources.getString(R.string.app_name) +
                "_confirmation_" + System.currentTimeMillis() + extension
    }

    override fun onFilesSave(confirmation: Confirmation) {
        UploadFilesJobIntentService.enqueueWork(this)
        getContributeSuccess().show(supportFragmentManager)
    }

    // region Permissions
    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (RequestCode.Permission.fromInt(requestCode)) {
            RequestCode.Permission.CAMERA_PICTURE -> if (hasCameraPermission()) {
                takePicture()
            }
            RequestCode.Permission.CAMERA_VIDEO -> if (hasCameraPermission()) {
                recordVideo()
            }
            RequestCode.Permission.AUDIO -> {
                openAudioGallery()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    // endregion

    override fun saveInstanceState(outState: Bundle) {
        outState.putParcelable(STATE_COMPLAINT, complaint)
        outState.putParcelable(STATE_CONFIRMATION, confirmation)
        if (fileAttached != null) {
            outState.putParcelable(STATE_FILE_ATTACHED, Uri.fromFile(fileAttached))
        }
        outState.putBoolean(STATE_IS_CONFIRMED, isConfirmed)
        outState.putBoolean(STATE_OPEN_PREVIEW, openPreview)
        outState.putBoolean(STATE_UPDATE_CONFIRMATION, updateConfirmation)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        complaint = savedInstanceState.getParcelable(STATE_COMPLAINT)
        confirmation = savedInstanceState.getParcelable(STATE_CONFIRMATION)
        val uri = savedInstanceState.getParcelable<Uri>(STATE_FILE_ATTACHED)
        if (uri != null && fileAttached == null) {
            fileAttached = File(uri.toString())
        }
        isConfirmed = savedInstanceState.getBoolean(STATE_IS_CONFIRMED)
        openPreview = savedInstanceState.getBoolean(STATE_OPEN_PREVIEW)
        updateConfirmation = savedInstanceState.getBoolean(STATE_UPDATE_CONFIRMATION)
    }

    override fun onLoadComplaint(complaint: Complaint) {
        this.complaint = complaint
        confirmation.complaintId = complaint.id!!
        detailsAdapter.complaint = complaint
    }

    companion object {
        private val EXTRA_COMPLAINT = "EXTRA_COMPLAINT"
        private val EXTRA_COMPLAINT_ID = "EXTRA_COMPLAINT_ID"
        private val EXTRA_IS_FROM_NOTIFICATION = "EXTRA_IS_FROM_NOTIFICATION"
        private val EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID"

        private val STATE_COMPLAINT = "STATE_COMPLAINT"
        private val STATE_CONFIRMATION = "STATE_CONFIRMATION"
        private val STATE_FILE_ATTACHED = "STATE_FILE_ATTACHED"
        private val STATE_IS_CONFIRMED = "STATE_IS_CONFIRMED"
        private val STATE_OPEN_PREVIEW = "STATE_OPEN_PREVIEW"
        private val STATE_UPDATE_CONFIRMATION = "STATE_UPDATE_CONFIRMATION"

        fun getIntent(context: Context, complaint: Complaint): Intent =
                Intent(context, ComplaintDetailsActivity::class.java)
                        .putExtra(EXTRA_COMPLAINT, complaint)

        fun getIntent(context: Context, complaintId: String, notificationId: String, isFromNotification: Boolean = false): Intent =
                Intent(context, ComplaintDetailsActivity::class.java)
                        .putExtra(EXTRA_COMPLAINT_ID, complaintId)
                        .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                        .putExtra(EXTRA_IS_FROM_NOTIFICATION, isFromNotification)
    }
}
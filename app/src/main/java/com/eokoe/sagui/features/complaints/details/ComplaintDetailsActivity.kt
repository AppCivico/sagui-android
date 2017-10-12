package com.eokoe.sagui.features.complaints.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import com.eokoe.sagui.widgets.dialog.AudioRecorderDialog
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
            val intent = ComplaintsActivity.getIntent(this, complaint?.enterprise!!,
                    complaint?.category!!, true)
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
                            onConfirmClickListener { dialog, _ ->
                                FirebaseMessaging.getInstance()
                                        .subscribeToTopic("complaint-${complaint!!.id}")
                                dialog.dismiss()
                            }
                            onCancelClickListener { dialog, _ ->
                                FirebaseMessaging.getInstance()
                                        .unsubscribeFromTopic("complaint-${complaint!!.id}")
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
                ContributeOptions.GALLERY_PICTURE -> openImageGallery()
                ContributeOptions.GALLERY_VIDEO -> openVideoGallery()
                ContributeOptions.AUDIO -> if (hasRecordAudioPermission()) {
                    recordAudio()
                } else {
                    requestRecordAudioPermission(RequestCode.Permission.AUDIO.value)
                }
                ContributeOptions.TAKE_PICTURE -> if (hasCameraPermission()) {
                    takePicture()
                } else {
                    requestCameraPermission(RequestCode.Permission.CAMERA_PICTURE.value)
                }
                ContributeOptions.RECORD_VIDEO -> if (hasCameraPermission()) {
                    recordVideo()
                } else {
                    requestCameraPermission(RequestCode.Permission.CAMERA_VIDEO.value)
                }
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

    private fun recordAudio() {
        AudioRecorderDialog
                .newInstance { audioFile ->
                    val privateFile = createNewFile(Files.Path.AUDIO_PATH, Files.Extensions.AMR)
                    audioFile.copyTo(privateFile, true)
                    audioFile.delete()
                    addFileToConfirmation(privateFile, false)
                    presenter.updateConfirmation(confirmation)
                }
                .show(supportFragmentManager)
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
                ErrorType.CONNECTION ->
                    "Error de conexão.\nPor favor verifique sua internet e tente novamente"
                ErrorType.CUSTOM -> error.friendlyMessage
                else -> "Ocorreu um erro inexperado.\nTente novamente mais tarde"
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (RequestCode.Intent.fromInt(requestCode)) {
            RequestCode.Intent.CAMERA_PICTURE -> if (resultCode == Activity.RESULT_OK &&
                    fileAttached?.exists() == true) {
                val privateFile = createNewFile(Files.Path.IMAGE_PATH, Files.Extensions.JPG)
                val inputFilePath = Uri.fromFile(fileAttached).getRealPath(this)!!
                FileUtil.compressImage(inputFilePath, privateFile)
                fileAttached?.delete()
                addFileToConfirmation(privateFile)
            }

            RequestCode.Intent.GALLERY_PICTURE -> if (resultCode == Activity.RESULT_OK &&
                    data?.data != null) {
                val privateFile = createNewFile(Files.Path.IMAGE_PATH, Files.Extensions.JPG)
                val tempFile = File.createTempFile(
                        getString(R.string.app_name) + "_confirmation_", Files.Extensions.JPG)
                data.data.copyTo(this, tempFile)
                FileUtil.compressImage(tempFile.path, privateFile)
                tempFile.delete()
                addFileToConfirmation(privateFile)
            }

            RequestCode.Intent.CAMERA_VIDEO -> if (resultCode == Activity.RESULT_OK &&
                    fileAttached?.exists() == true) {
                val privateFile = createNewFile(Files.Path.VIDEO_PATH, Files.Extensions.MP4)
                fileAttached!!.copyTo(privateFile, true)
                fileAttached!!.delete()
                addFileToConfirmation(privateFile)
            }

            RequestCode.Intent.GALLERY_VIDEO -> if (resultCode == Activity.RESULT_OK &&
                    data?.data != null) {
                val privateFile = createNewFile(Files.Path.VIDEO_PATH, Files.Extensions.MP4)
                data.data.copyTo(this, privateFile)
                addFileToConfirmation(privateFile)
            }

            RequestCode.Intent.AUDIO -> if (resultCode == Activity.RESULT_OK &&
                    data?.data != null) {
                val privateFile = createNewFile(Files.Path.AUDIO_PATH)
                data.data.copyTo(this, privateFile)
                addFileToConfirmation(privateFile)
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

    private fun createNewFile(path: String, suffix: String = "") =
            File(File(filesDir, path), generateFilename(suffix))

    private fun generateFilename(suffix: String) =
            resources.getString(R.string.app_name) +
                    "_confirmation_" + System.currentTimeMillis() + suffix

    private fun addFileToConfirmation(file: File, preview: Boolean = true) {
        val type = contentResolver.getType(file.getUri(this))
        confirmation.files.add(Asset(localPath = file.absolutePath, type = type))
        openPreview = preview
    }

    override fun onFilesSave(confirmation: Confirmation) {
        UploadFilesJobIntentService.enqueueWork(this)
        getContributeSuccess().show(supportFragmentManager)
    }

    // region Permissions
    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        val permissionGranted = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
        when (RequestCode.Permission.fromInt(requestCode)) {
            RequestCode.Permission.CAMERA_PICTURE -> if (permissionGranted) {
                takePicture()
            }
            RequestCode.Permission.CAMERA_VIDEO -> if (permissionGranted) {
                recordVideo()
            }
            RequestCode.Permission.AUDIO -> if (permissionGranted) {
                recordAudio()
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

        fun getIntent(context: Context, complaintId: String, notificationId: String,
                      isFromNotification: Boolean = false): Intent =
                Intent(context, ComplaintDetailsActivity::class.java)
                        .putExtra(EXTRA_COMPLAINT_ID, complaintId)
                        .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                        .putExtra(EXTRA_IS_FROM_NOTIFICATION, isFromNotification)
    }
}
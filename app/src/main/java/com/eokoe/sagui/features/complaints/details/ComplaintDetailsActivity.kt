package com.eokoe.sagui.features.complaints.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Confirmation
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.ErrorType
import com.eokoe.sagui.extensions.errorType
import com.eokoe.sagui.extensions.friendlyMessage
import com.eokoe.sagui.extensions.getRealPath
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.show_asset.ShowAssetActivity
import com.eokoe.sagui.services.upload_file.UploadFilesJobIntentService
import com.eokoe.sagui.utils.AUTHORITY
import com.eokoe.sagui.utils.FileUtil
import com.eokoe.sagui.utils.IMAGE_PATH
import com.eokoe.sagui.utils.LogUtil
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_complaint_details.*
import java.io.File

/**
 * @author Pedro Silva
 * @since 28/09/17
 */
class ComplaintDetailsActivity : BaseActivity(),
        ConfirmContract.View, ViewPresenter<ConfirmContract.Presenter> {
    override fun onFilesSave(confirmation: Confirmation) {
        UploadFilesJobIntentService.enqueueWork(this)
        getContributeSuccess().show(supportFragmentManager)
    }

    override lateinit var presenter: ConfirmContract.Presenter
    private lateinit var complaint: Complaint
    private var confirmation = Confirmation()
    private lateinit var loadingDialog: LoadingDialog
    private var isConfirmed: Boolean = false
    private var fileAttached: File? = null
    private var openPreview: Boolean = false
    private var updateConfirmation: Boolean = false

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
        if (isConfirmed) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        complaint = intent.extras.getParcelable(EXTRA_COMPLAINT)
        confirmation.complaintId = complaint.id!!
        presenter = ConfirmPresenter(SaguiModelImpl())
        loadingDialog = LoadingDialog.newInstance(getString(R.string.loading_confirm_complaint))
        btnConfirm.setOnClickListener {
//            openContributeDialog()
            getConfirmDialog().show(supportFragmentManager)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        rvComplaintDetails.setHasFixedSize(true)
        val detailsAdapter = ComplaintDetailsAdapter(complaint)
        detailsAdapter.onImageClickListener = object : AssetsAdapter.OnItemClickListener {
            override fun onItemClick(asset: Asset) {
                val intent = ShowAssetActivity.getIntent(this@ComplaintDetailsActivity, asset)
                startActivity(intent)
            }
        }
        rvComplaintDetails.adapter = detailsAdapter
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
            onConfirmClickListener { dialog, which ->
                presenter.confirmComplaint(confirmation)
            }
        }
    }

    private fun getContributeDialog(): AlertDialogFragment {
        // TODO change to contribute
        return AlertDialogFragment.create(this) {
            titleRes = R.string.confirmed
            messageRes = R.string.msg_contribute
            positiveTextRes = R.string.contribute
            negativeTextRes = R.string.cancel
            cancelable = true
            onConfirmClickListener { dialog, which ->
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
        getAlertList(resources.getStringArray(R.array.contribute_options)) { dialog, position ->
            when (position) {
                0 -> {
                    if (hasCameraPermission()) {
                        takePicture()
                    } else {
                        requestCameraPermission()
                    }
                }
                1 -> {
                    if (hasReadExternalStoragePermission()) {
                        openGallery()
                    } else {
                        requestReadExternalStoragePermission()
                    }
                }
            }
            dialog.dismiss()
        }.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/* video/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_PHOTO_GALLERY)
        }
    }

    private fun requestCameraPermission() {
        // TODO handle permission not granted
        if (!hasCameraPermission()) {
            requestCameraPermission(R.string.title_request_camera_permission,
                    R.string.message_request_camera_permission,
                    REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun takePicture() {
        fileAttached = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                generateFilename()
        )
        val file = FileProvider.getUriForFile(this, AUTHORITY, fileAttached)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
        grantUriRwPermissions(intent, file)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }
    }

    private fun grantUriRwPermissions(intent: Intent, file: Uri?) {
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val resInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resInfoList
                .map { it.activityInfo.packageName }
                .forEach {
                    grantUriPermission(it, file,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
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

    private fun requestReadExternalStoragePermission() {
        // TODO handle permission not granted
        if (!hasReadExternalStoragePermission()) {
            requestReadExternalStoragePermission(R.string.title_request_read_external_storage_permission,
                    R.string.message_request_read_external_storage_permission,
                    REQUEST_IMAGE_VIDEO_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LogUtil.debug(this, "" + requestCode)
        when (requestCode) {
            REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && fileAttached?.exists() == true) {
                val imagePath = File(filesDir, IMAGE_PATH)
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
                confirmation.files.add(Asset(localPath = privateFile.path))
                openPreview = true
            }
            REQUEST_CODE_PHOTO_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val imagePath = File(filesDir, IMAGE_PATH)
                val privateFile = File(
                        imagePath,
                        generateFilename()
                )
                contentResolver.openInputStream(uri).use { inputStream ->
                    if (inputStream != null) {
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        FileUtil.compressImage(bitmap, privateFile)
                        confirmation.files.add(Asset(localPath = privateFile.path))
                    }
                }
                openPreview = true
            }
            REQUEST_PREVIEW_ASSET -> if (resultCode == Activity.RESULT_OK) {
                updateConfirmation = true
            } else {
                confirmation.files.clear()
            }
        }
    }

    private fun openPreview() {
        val intent = ShowAssetActivity.getIntent(this, confirmation.files, showSendButton = true)
        startActivityForResult(intent, REQUEST_PREVIEW_ASSET)
    }

    private fun generateFilename(): String {
        return resources.getString(R.string.app_name) +
                "_confirmation_" + System.currentTimeMillis() + ".jpg"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> if (hasCameraPermission()) {
                takePicture()
            }
            REQUEST_IMAGE_VIDEO_PERMISSION -> if (hasReadExternalStoragePermission()) {
                openGallery()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private val EXTRA_COMPLAINT = "EXTRA_COMPLAINT"
        private val REQUEST_PREVIEW_ASSET = 0
        private val REQUEST_CODE_PHOTO = 1
        private val REQUEST_CODE_PHOTO_GALLERY = 2
        private val REQUEST_CAMERA_PERMISSION = 1
        private val REQUEST_IMAGE_VIDEO_PERMISSION = 2

        fun getIntent(context: Context, complaint: Complaint): Intent =
                Intent(context, ComplaintDetailsActivity::class.java)
                        .putExtra(EXTRA_COMPLAINT, complaint)
    }
}
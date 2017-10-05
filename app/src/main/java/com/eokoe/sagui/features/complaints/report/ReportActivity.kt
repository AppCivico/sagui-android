package com.eokoe.sagui.features.complaints.report

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.ErrorType
import com.eokoe.sagui.extensions.copyTo
import com.eokoe.sagui.extensions.errorType
import com.eokoe.sagui.extensions.getRealPath
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.report.ReportAdapter.ItemType
import com.eokoe.sagui.features.complaints.report.pin.PinActivity
import com.eokoe.sagui.services.upload_file.UploadFilesJobIntentService
import com.eokoe.sagui.utils.*
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_report.*
import java.io.File


/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportActivity : BaseActivity(), ReportAdapter.OnItemClickListener,
        ReportContract.View, ViewPresenter<ReportContract.Presenter> {

    override lateinit var presenter: ReportContract.Presenter
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var progressDialog: LoadingDialog
    private val complaint = Complaint()
    private var enterprise: Enterprise? = null
    private var fileAttached: File? = null
    private var category: Category? = null

    // region Lifecycle
    override fun onResume() {
        super.onResume()
        reportAdapter.setComplaint(complaint)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
    }
    // endregion

    // region Initialization and setup
    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        presenter = ReportPresenter(SaguiModelImpl())
        progressDialog = LoadingDialog.newInstance("Reportando problema")

        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        complaint.enterpriseId = enterprise?.id
        category = intent.extras?.getParcelable(EXTRA_CATEGORY)
        complaint.categoryId = category?.id
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvReport.setHasFixedSize(false)
        reportAdapter = ReportAdapter()
        rvReport.adapter = reportAdapter
        reportAdapter.onItemClickListener = this
        reportAdapter.titleChangeSubject.subscribe {
            complaint.title = it
        }
        reportAdapter.descriptionChangeSubject.subscribe {
            complaint.description = it
        }
    }
    // endregion

    // region Events
    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onItemClick(itemType: ItemType) {
        when (itemType) {
            ItemType.LOCATION -> {
                val intent = PinActivity.getIntent(this@ReportActivity, enterprise!!,
                        complaint.location, complaint.address)
                startActivityForResult(intent, REQUEST_CODE_LOCATION)
            }
            ItemType.CAMERA -> {
                if (hasCameraPermission()) {
                    openCamera()
                } else {
                    requestCameraPermission()
                }
            }
            ReportAdapter.ItemType.INSERT_PHOTO_VIDEO -> {
                if (hasReadExternalStoragePermission()) {
                    openGallery()
                } else {
                    requestReadExternalStoragePermission(REQUEST_IMAGE_VIDEO_PERMISSION)
                }
            }
            ReportAdapter.ItemType.INSERT_AUDIO -> {
                if (hasReadExternalStoragePermission()) {
                    openAudioGallery()
                } else {
                    requestReadExternalStoragePermission(REQUEST_AUDIO_GALLERY_PERMISSION)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_ok) {
            presenter.saveComplaint(complaint)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    // endregion

    // region Actions and intents
    private fun openCamera() {
        val alert = AlertDialog.Builder(this)
                .setItems(R.array.camera_options, { dialog, position ->
                    val intent: Intent
                    val requestCode: Int
                    if (position == 0) {
                        intent = takePictureIntent()
                        requestCode = REQUEST_CODE_CAMERA
                    } else {
                        intent = recordVideoIntent()
                        requestCode = REQUEST_CODE_VIDEO
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivityForResult(intent, requestCode)
                    }
                    dialog.dismiss()
                })
                .create()
        alert.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/* video/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }
    }

    private fun openAudioGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_AUDIO)
        }
    }

    // region Intents
    private fun takePictureIntent(): Intent {
        val filename = resources.getString(R.string.app_name) +
                "_complaint_" + System.currentTimeMillis() + ".jpg"
        fileAttached = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                filename
        )
        val file = FileProvider.getUriForFile(this, AUTHORITY, fileAttached)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
        grantUriRwPermissions(intent, file)
        return intent
    }

    private fun recordVideoIntent(): Intent {
        val filename = resources.getString(R.string.app_name) +
                "_complaint_" + System.currentTimeMillis() + ".mp4"
        fileAttached = File(
                getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                filename
        )
        val file = FileProvider.getUriForFile(this, AUTHORITY, fileAttached)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        grantUriRwPermissions(intent, file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
        return intent
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
    // endregion
    // endregion

    override fun showError(error: Throwable) {
        hideLoading()
        AlertDialogFragment
                .create(this) {
                    title = "Falha ao reportar problema"
                    message = if (error.errorType == ErrorType.CONNECTION)
                        "Por favor verifique sua internet e tente novamente"
                    else "Ocorreu um erro inexperado.\nTente novamente mais tarde"
                }
                .show(supportFragmentManager)
    }

    override fun onSaveSuccess(complaint: Complaint) {
        hideKeyboard()
        val data = Intent()
        data.putExtra(RESULT_LAT_LONG, complaint.location)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun showLoading() {
        progressDialog.show(supportFragmentManager)
    }

    override fun hideLoading() {
        progressDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (resultCode == Activity.RESULT_OK) {
                complaint.location = data?.getParcelableExtra(PinActivity.RESULT_LOCATION)
                complaint.address = data?.getStringExtra(PinActivity.RESULT_ADDRESS)
            }
            REQUEST_CODE_CAMERA -> if (resultCode == Activity.RESULT_OK && fileAttached?.exists() == true) {
                val imagePath = File(filesDir, IMAGE_PATH)
                val privateFile = File(
                        imagePath,
                        fileAttached!!.name
                )
                LogUtil.debug(this, "Old size: " + fileAttached?.length())
                val inputFilePath = Uri.fromFile(fileAttached).getRealPath(this)!!
                FileUtil.compressImage(inputFilePath, privateFile)
                val uriImage = data?.data ?: Uri.fromFile(fileAttached)
                try {
                    fileAttached?.delete()
                    contentResolver.delete(uriImage, null, null)
                } catch (error: Exception) {
                    error.printStackTrace()
                }
                complaint.files.add(Asset(Uri.fromFile(privateFile)))
                LogUtil.debug(this, "New size: " + privateFile.length())
                LogUtil.debug(this, "privateFile exists: " + privateFile.exists())
                LogUtil.debug(this, "pictureFile exists: " + fileAttached?.exists())
            }
            REQUEST_CODE_VIDEO -> if (resultCode == Activity.RESULT_OK && fileAttached?.exists() == true) {
                val videoPath = File(filesDir, VIDEO_PATH)
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
                complaint.files.add(Asset(Uri.fromFile(privateFile)))
            }
            REQUEST_CODE_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val imagePath = File(filesDir, IMAGE_PATH)
                val filename = resources.getString(R.string.app_name) +
                        "_complaint_" + System.currentTimeMillis() + ".jpg"
                val privateFile = File(
                        imagePath,
                        filename
                )
                contentResolver.openInputStream(uri).use { inputStream ->
                    if (inputStream != null) {
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        FileUtil.compressImage(bitmap, privateFile)
                        complaint.files.add(Asset(Uri.fromFile(privateFile)))
                    }
                }
            }
            REQUEST_CODE_AUDIO -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val audioPath = File(filesDir, AUDIO_PATH)
                val privateFile = File(
                        audioPath,
                        "_complaint_" + System.currentTimeMillis() + ".amr"
                )
                uri.copyTo(this, privateFile)
                complaint.files.add(Asset(Uri.fromFile(privateFile)))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> if (hasCameraPermission()) {
                openCamera()
            }
            REQUEST_IMAGE_VIDEO_PERMISSION -> if (hasReadExternalStoragePermission()) {
                openGallery()
            }
            REQUEST_AUDIO_GALLERY_PERMISSION -> if (hasReadExternalStoragePermission()) {
                openAudioGallery()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun hasCameraPermission() = hasPermission(Manifest.permission.CAMERA)

    private fun hasReadExternalStoragePermission() =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
                    hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun requestCameraPermission() {
        // TODO handle permission not granted
        if (!hasCameraPermission()) {
            requestPermission(R.string.title_request_camera_permission, R.string.message_request_camera_permission, REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA)
        }
    }

    @SuppressLint("InlinedApi")
    private fun requestReadExternalStoragePermission(requestCode: Int) {
        // TODO handle permission not granted
        if (!hasReadExternalStoragePermission()) {
            requestPermission(
                    R.string.title_request_read_external_storage_permission,
                    R.string.message_request_read_external_storage_permission,
                    requestCode,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun uploadAssets() {
        UploadFilesJobIntentService.enqueueWork(this)
    }

    companion object {
        private val REQUEST_CODE_LOCATION = 1
        private val REQUEST_CODE_CAMERA = 2
        private val REQUEST_CODE_VIDEO = 3
        private val REQUEST_CODE_GALLERY = 4
        private val REQUEST_CODE_AUDIO = 5
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val REQUEST_CAMERA_PERMISSION = 1
        private val REQUEST_IMAGE_VIDEO_PERMISSION = 2
        private val REQUEST_AUDIO_GALLERY_PERMISSION = 3
        val RESULT_LAT_LONG = "RESULT_LAT_LONG"

        fun getIntent(context: Context, enterprise: Enterprise, category: Category?): Intent =
                Intent(context, ReportActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORY, category)
    }
}
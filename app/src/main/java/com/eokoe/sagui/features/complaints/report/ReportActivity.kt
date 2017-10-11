package com.eokoe.sagui.features.complaints.report

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
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.*
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.report.ReportAdapter.ItemType
import com.eokoe.sagui.features.complaints.report.pin.PinActivity
import com.eokoe.sagui.services.upload_file.UploadFilesJobIntentService
import com.eokoe.sagui.utils.FileUtil
import com.eokoe.sagui.utils.Files
import com.eokoe.sagui.utils.RequestCode
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

    private var complaint = Complaint()
    private var fileAttached: File? = null

    private var enterprise: Enterprise? = null
    private var category: Category? = null
    private var categories: List<Category>? = null

    // region Lifecycle
    override fun onResume() {
        super.onResume()
        reportAdapter.complaint = complaint
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
        if (complaint.category == null) {
            complaint.category = category
        }
        categories = intent.extras?.getParcelableArrayList(EXTRA_CATEGORIES)
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvReport.setHasFixedSize(false)
        reportAdapter = ReportAdapter(complaint, categories != null)
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
                startActivityForResult(intent, RequestCode.Intent.LOCATION.value)
            }
            ItemType.CAMERA -> {
                if (hasCameraPermission()) {
                    openCamera()
                } else {
                    requestCameraPermission(RequestCode.Permission.CAMERA.value)
                }
            }
            ItemType.INSERT_PHOTO_VIDEO -> openGallery()
            ItemType.INSERT_AUDIO -> openAudioGallery()
            ItemType.CATEGORY -> {
                openCategories()
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
        getAlertList(resources.getStringArray(R.array.camera_options)) { dialog, position ->
            val intent: Intent
            val requestCode: RequestCode.Intent
            if (position == 0) {
                intent = takePictureIntent()
                requestCode = RequestCode.Intent.CAMERA_PICTURE
            } else {
                intent = recordVideoIntent()
                requestCode = RequestCode.Intent.CAMERA_VIDEO
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, requestCode.value)
            }
            dialog.dismiss()
        }.show()
    }

    private fun openCategories() {
        val categoriesStr = categories!!.map {
            it.name
        }
        getAlertList(categoriesStr.toTypedArray()) { dialog, position ->
            complaint.category = categories!![position]
            reportAdapter.complaint = complaint
            dialog.dismiss()
        }.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.GALLERY_PICTURE.value)
        }
    }

    private fun openAudioGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.AUDIO.value)
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
        val file = fileAttached!!.getUri(this)
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
        val file = fileAttached!!.getUri(this)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        grantUriRwPermissions(intent, file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
        return intent
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
        data.putExtra(RESULT_COMPLAINT_ID, complaint.id)
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

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (RequestCode.Intent.fromInt(requestCode)) {
            RequestCode.Intent.LOCATION -> if (resultCode == Activity.RESULT_OK) {
                complaint.location = data?.getParcelableExtra(PinActivity.RESULT_LOCATION)
                complaint.address = data?.getStringExtra(PinActivity.RESULT_ADDRESS)
            }
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
                complaint.files.add(Asset(localPath = privateFile.path, type = "image/*"))
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
                complaint.files.add(Asset(localPath = privateFile.path, type = "video/*"))
            }
            RequestCode.Intent.GALLERY_PICTURE -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val imagePath = File(filesDir, Files.Path.IMAGE_PATH)
                val filename = resources.getString(R.string.app_name) +
                        "_complaint_" + System.currentTimeMillis() + ".jpg"
                val privateFile = File(
                        imagePath,
                        filename
                )
                val tempFile = File.createTempFile(getString(R.string.app_name) + "_complaint_", ".jpg")
                uri.copyTo(this, tempFile)
                FileUtil.compressImage(tempFile.path, privateFile)
                complaint.files.add(Asset(localPath = privateFile.path, type = "image/*"))
            }
            RequestCode.Intent.AUDIO -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                val audioPath = File(filesDir, Files.Path.AUDIO_PATH)
                val privateFile = File(
                        audioPath,
                        "_complaint_" + System.currentTimeMillis()
                )
                uri.copyTo(this, privateFile)
                complaint.files.add(Asset(localPath = privateFile.path, type = "audio/*"))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (RequestCode.Permission.fromInt(requestCode)) {
            RequestCode.Permission.CAMERA -> if (hasCameraPermission()) {
                openCamera()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun uploadAssets() {
        UploadFilesJobIntentService.enqueueWork(this)
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putParcelable(STATE_COMPLAINT, complaint)
        if (fileAttached != null) {
            outState.putParcelable(STATE_FILE_ATTACHED, Uri.fromFile(fileAttached))
        }
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        complaint = savedInstanceState.getParcelable(STATE_COMPLAINT)
        val uri = savedInstanceState.getParcelable<Uri>(STATE_FILE_ATTACHED)
        if (uri != null && fileAttached == null) {
            fileAttached = File(uri.toString())
        }
    }

    companion object {
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val EXTRA_CATEGORIES = "EXTRA_CATEGORIES"

        val RESULT_COMPLAINT_ID = "RESULT_COMPLAINT_ID"
        val RESULT_LAT_LONG = "RESULT_LAT_LONG"

        private val STATE_COMPLAINT = "STATE_COMPLAINT"
        private val STATE_FILE_ATTACHED = "STATE_FILE_ATTACHED"

        fun getIntent(context: Context, enterprise: Enterprise, category: Category): Intent =
                Intent(context, ReportActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORY, category)

        fun getIntent(context: Context, enterprise: Enterprise, categories: ArrayList<Category>): Intent =
                Intent(context, ReportActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORIES, categories)
    }
}
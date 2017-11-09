package com.eokoe.sagui.features.complaints.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.*
import com.eokoe.sagui.features.asset.ShowAssetActivity
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.report.ReportAdapter.ItemType
import com.eokoe.sagui.features.complaints.report.pin.PinActivity
import com.eokoe.sagui.services.upload.UploadFilesJobIntentService
import com.eokoe.sagui.utils.FileUtil
import com.eokoe.sagui.utils.Files
import com.eokoe.sagui.utils.LogUtil
import com.eokoe.sagui.utils.RequestCode
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.AudioRecorderDialog
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_report.*
import java.io.File


/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportActivity : BaseActivity(),
        ReportAdapter.OnItemClickListener, ThumbnailAdapter.OnItemClickListener,
        ReportContract.View, ViewPresenter<ReportContract.Presenter>, View.OnClickListener {

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
        presenter = ReportPresenter(SaguiModelImpl(this))
        progressDialog = LoadingDialog.newInstance(getString(R.string.reporting_problem))
        val complaint: Complaint? = intent.extras?.getParcelable(EXTRA_COMPLAINT)
        if (complaint != null) {
            this.complaint = complaint
        }
        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        this.complaint.enterprise = enterprise
        this.complaint.enterpriseId = enterprise?.id
        category = intent.extras?.getParcelable(EXTRA_CATEGORY)
        if (this.complaint.category == null) {
            this.complaint.category = category
        }
        categories = intent.extras?.getParcelableArrayList(EXTRA_CATEGORIES)
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
        ibAddImage.setOnClickListener(this)
        ibAddVideo.setOnClickListener(this)
        ibAddAudio.setOnClickListener(this)
    }

    private fun setupRecyclerView() {
        rvReport.setHasFixedSize(false)
        reportAdapter = ReportAdapter(complaint, categories != null)
        reportAdapter.onItemClickListener = this
        reportAdapter.onAssetClickListener = this
        rvReport.adapter = reportAdapter
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
            ItemType.CATEGORY -> {
                openCategories()
            }
        }
    }

    override fun onItemClick(file: Asset) {
        getAlertList(arrayOf(getString(R.string.visualize), getString(R.string.remove))) { dialog, position ->
            if (position == 0) {
                val intent = ShowAssetActivity.getIntent(this@ReportActivity,
                        complaint.files.toList(), complaint.files.indexOf(file))
                startActivity(intent)
            } else if (position == 1) {
                try {
                    file.uri.toFile(this)?.delete()
                } catch (error: Exception) {
                    LogUtil.error(this, error)
                }
                complaint.files.remove(file)
                reportAdapter.complaint = complaint
            }
            dialog.dismiss()
        }.show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ibAddImage -> addImage()
            R.id.ibAddVideo -> addVideo()
            R.id.ibAddAudio -> if (hasRecordAudioPermission()) {
                recordAudio()
            } else {
                requestRecordAudioPermission(RequestCode.Permission.AUDIO.value)
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

    override fun onBackPressed() {
        complaint.files.forEach {
            try {
                it.uri.toFile(applicationContext)?.delete()
            } catch (error: Exception) {
                LogUtil.error(this, error)
            }
        }
        super.onBackPressed()
    }
    // endregion

    // region Actions and intents
    private fun addImage() {
        getAlertList(resources.getStringArray(R.array.image_options)) { dialog, position ->
            if (position == 0) {
                if (hasCameraPermission()) {
                    takePicture()
                } else {
                    requestCameraPermission(RequestCode.Permission.CAMERA_PICTURE.value)
                    return@getAlertList
                }
            } else {
                openImageGallery()
            }
            dialog.dismiss()
        }.show()
    }

    private fun addVideo() {
        getAlertList(resources.getStringArray(R.array.video_options)) { dialog, position ->
            if (position == 0) {
                if (hasCameraPermission()) {
                    recordVideo()
                } else {
                    requestCameraPermission(RequestCode.Permission.CAMERA_VIDEO.value)
                    return@getAlertList
                }
            } else {
                openVideoGallery()
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

    private fun openImageGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")

        startActivityForResult(intent, RequestCode.Intent.GALLERY_PICTURE.value)
    }

    private fun openVideoGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("video/*")

        startActivityForResult(intent, RequestCode.Intent.GALLERY_VIDEO.value)
    }

    private fun recordAudio() {
        AudioRecorderDialog
                .newInstance { audioFile ->
                    val privateFile = createNewFile(Files.Path.AUDIO_PATH, Files.Extensions.AAC)
                    audioFile.copyTo(privateFile, true)
                    audioFile.delete()
                    addFileToComplaint(privateFile)
                    reportAdapter.complaint = complaint
                }
                .show(supportFragmentManager)
    }

    private fun takePicture() {
        fileAttached = File(
                getExternalFilesDir(Environment.DIRECTORY_MOVIES),
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
        grantUriRwPermissions(intent, file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RequestCode.Intent.CAMERA_VIDEO.value)
        }
    }
    // endregion

    override fun showError(error: Throwable) {
        hideLoading()
        AlertDialogFragment
                .create(this) {
                    title = "Falha ao reportar problema"
                    message = if (error.errorType == ErrorType.CONNECTION)
                        "Por favor verifique sua internet e tente novamente"
                    else "Ops.. ocorreu um erro inexperado.\nTente novamente mais tarde"
                }
                .show(supportFragmentManager)
    }

    override fun isValidForm(): Boolean {
        var msgErr = ""
        if (complaint.description.isEmpty()) {
            msgErr += "\t- Descrição\n"
        }
        if (complaint.title.isEmpty()) {
            msgErr += "\t- Título\n"
        }
        if (complaint.category == null) {
            msgErr += "\t- Categoria\n"
        }
        val isValid = msgErr.isEmpty()
        if (!isValid) {
            AlertDialogFragment
                    .create(this) {
                        title = "Falha ao reportar problema"
                        message = "Preencha os seguintes campos:\n" + msgErr.substring(0, msgErr.length - 1)
                    }
                    .show(supportFragmentManager)
        }
        return isValid
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

            RequestCode.Intent.CAMERA_PICTURE -> if (resultCode == Activity.RESULT_OK &&
                    fileAttached?.exists() == true) {
                val privateFile = createNewFile(Files.Path.IMAGE_PATH, Files.Extensions.JPG)
                val inputFilePath = Uri.fromFile(fileAttached).getRealPath(this)!!
                FileUtil.compressImage(inputFilePath, privateFile)
                fileAttached?.delete()
                addFileToComplaint(privateFile)
            }

            RequestCode.Intent.CAMERA_VIDEO -> if (resultCode == Activity.RESULT_OK) {
                val privateFile = createNewFile(Files.Path.VIDEO_PATH, Files.Extensions.MP4)
                when {
                    fileAttached?.exists() == true -> {
                        fileAttached!!.copyTo(privateFile, true)
                        fileAttached!!.delete()
                    }
                    data?.data != null -> data.data.copyTo(this, privateFile)
                    else -> return
                }
                addFileToComplaint(privateFile)
            }

            RequestCode.Intent.GALLERY_PICTURE -> if (resultCode == Activity.RESULT_OK &&
                    data != null) {
                val privateFile = createNewFile(Files.Path.IMAGE_PATH, Files.Extensions.JPG)
                val tempFile = File.createTempFile(
                        getString(R.string.app_name) + "_complaint_", Files.Extensions.JPG)
                data.data.copyTo(this, tempFile)
                FileUtil.compressImage(tempFile.absolutePath, privateFile)
                tempFile.delete()
                addFileToComplaint(privateFile)
            }

            RequestCode.Intent.GALLERY_VIDEO -> if (resultCode == Activity.RESULT_OK &&
                    data != null) {
                val privateFile = createNewFile(Files.Path.VIDEO_PATH, Files.Extensions.MP4)
                data.data.copyTo(this, privateFile)
                addFileToComplaint(privateFile)
            }

            RequestCode.Intent.AUDIO -> if (resultCode == Activity.RESULT_OK && data != null) {
                val privateFile = createNewFile(Files.Path.AUDIO_PATH)
                data.data.copyTo(this, privateFile)
                addFileToComplaint(privateFile)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

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

    override fun uploadAssets() {
        UploadFilesJobIntentService.enqueueWork(this)
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putParcelable(STATE_COMPLAINT, complaint)
        if (fileAttached != null) {
            outState.putParcelable(STATE_FILE_ATTACHED, Uri.fromFile(fileAttached))
        }
    }

    private fun createNewFile(path: String, suffix: String = "") =
            File(File(filesDir, path), generateFilename(suffix))

    private fun generateFilename(suffix: String = "") =
            resources.getString(R.string.app_name) +
                    "_complaint_" + System.currentTimeMillis() + suffix

    private fun addFileToComplaint(file: File) {
        val type = contentResolver.getType(file.getUri(this))
        complaint.files.add(Asset(localPath = file.absolutePath, type = type))
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
        private val EXTRA_COMPLAINT = "EXTRA_COMPLAINT"

        val RESULT_COMPLAINT_ID = "RESULT_COMPLAINT_ID"
        val RESULT_LAT_LONG = "RESULT_LAT_LONG"

        private val STATE_COMPLAINT = "STATE_COMPLAINT"
        private val STATE_FILE_ATTACHED = "STATE_FILE_ATTACHED"

        fun getIntent(context: Context, enterprise: Enterprise, category: Category): Intent =
                Intent(context, ReportActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORY, category)

        fun getIntent(context: Context, complaint: Complaint): Intent =
                Intent(context, ReportActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, complaint.enterprise)
                        .putExtra(EXTRA_CATEGORY, complaint.category)
                        .putExtra(EXTRA_COMPLAINT, complaint)

        fun getIntent(context: Context, enterprise: Enterprise,
                      categories: ArrayList<Category>): Intent =
                Intent(context, ReportActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORIES, categories)
    }
}
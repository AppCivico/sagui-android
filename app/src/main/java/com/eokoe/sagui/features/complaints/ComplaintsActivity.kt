package com.eokoe.sagui.features.complaints

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.entities.LatLong
import com.eokoe.sagui.extensions.invisibleSlidingBottom
import com.eokoe.sagui.extensions.isVisible
import com.eokoe.sagui.extensions.setup
import com.eokoe.sagui.extensions.showSlidingTop
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.ViewLocation
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.categories.CategoriesActivity
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsActivity
import com.eokoe.sagui.features.complaints.report.ReportActivity
import com.eokoe.sagui.utils.LocationHelper
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_complaints.*
import kotlinx.android.synthetic.main.content_box_complaint_details.*
import org.koin.android.ext.android.inject


/**
 * @author Pedro Silva
 */
class ComplaintsActivity : BaseActivityNavDrawer(), OnMapReadyCallback,
        ComplaintsContract.View, ViewPresenter<ComplaintsContract.Presenter>,
        ViewLocation, LocationHelper.OnLocationReceivedListener, GoogleMap.OnMapClickListener,
        ClusterManager.OnClusterClickListener<ComplaintItem>,
        ClusterManager.OnClusterItemClickListener<ComplaintItem> {

    override val presenter by inject<ComplaintsContract.Presenter>()

    private var map: GoogleMap? = null
    override var locationHelper = LocationHelper()
    private lateinit var clusterManager: ClusterManager<ComplaintItem>
    private lateinit var mapFragment: SupportMapFragment

    private var complaints: List<Complaint>? = null
    private var complaintSelected: String? = null
    private var insertedComplaintId: String? = null
    private var isFromNotification: Boolean = false

    private var latLngBounds: LatLngBounds? = null
    private var lastLatLong: LatLong? = null
    private var updateMarkers = false
    private var allowNotifications = false

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaints)
    }

    override fun onResume() {
        super.onResume()
        navigationView.setCheckedItem(R.id.nav_complaints)
        if (map != null && (lastLatLong != null || updateMarkers)) {
            updateMap()
        }
        if (insertedComplaintId != null) {
            showAlertCongratulations()
        }
    }
    // endregion

    // region Setup and initialization
    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        category = intent.extras?.getParcelable(EXTRA_CATEGORY)
        categories = intent.extras?.getParcelableArrayList(EXTRA_CATEGORIES)
        isFromNotification = intent.extras.getBoolean(EXTRA_IS_FROM_NOTIFICATION)
        title = enterprise?.name
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    override fun init(savedInstanceState: Bundle?) {
        mapFragment.getMapAsync(this)
        fabAdd.setOnClickListener {
            val intent = if (category != null) {
                ReportActivity.getIntent(this, enterprise!!, category!!)
            } else {
                ReportActivity.getIntent(this, enterprise!!, categories!!)
            }
            startActivityForResult(intent, REQUEST_CREATE_REPORT)
            hideBoxDetails()
        }
        rlBoxComplaint.post {
            rlBoxComplaint.translationY = rlBoxComplaint.height.toFloat()
        }
        fabAdd.show()
        btnConfirm.setOnClickListener {
            viewDetails()
        }
    }
    // endregion

    // region Activity listeners
    override fun onBackPressed() {
        if (!hideBoxDetails()) {
            if (isFromNotification) {
                val intent = CategoriesActivity.getIntent(this, enterprise!!)
                startActivity(intent)
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CREATE_REPORT) {
            if (resultCode == Activity.RESULT_OK) {
                insertedComplaintId = data?.getStringExtra(ReportActivity.RESULT_COMPLAINT_ID)
                lastLatLong = data?.getParcelableExtra(ReportActivity.RESULT_LAT_LONG)
            }
        } else if (requestCode == REQUEST_CONFIRM_REPORT) {
            updateMarkers = resultCode == Activity.RESULT_OK
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    // endregion

    // region Map listeners
    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setup(this)

        clusterManager = ClusterManager(this, map)
        clusterManager.renderer = ComplaintClusterRender(map, this, clusterManager)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.setOnClusterClickListener(this)

        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)
        map.setOnMapClickListener(this)

        markEnterpriseLocation(map, enterprise!!)
        if (complaints == null || complaints!!.isEmpty()) {
            presenter.list(enterprise!!, category)
        } else {
            loadComplaints(complaints!!)
        }
        if (!requestLocation()) {
            requestLocationPermission(R.string.title_request_location_permission,
                    R.string.message_request_location_permission, REQUEST_PERMISSION_LOCATION)
        }
    }

    override fun onClusterClick(cluster: Cluster<ComplaintItem>): Boolean {
        val builder = LatLngBounds.Builder()
        cluster.items.forEach { item ->
            builder.include(item.position)
        }
        map!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0))
        return true
    }

    override fun onClusterItemClick(item: ComplaintItem): Boolean {
        showBoxDetails(map!!, item.complaint)
        return true
    }

    override fun onMapClick(latLng: LatLng?) {
        hideBoxDetails()
    }

    override fun onLocationReceived(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (latLngBounds?.contains(latLng) == true) {
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }
    // endregion

    // region Box details
    private fun hideBoxDetails(): Boolean {
        complaintSelected = null
        if (rlBoxComplaint.isVisible) {
            rlBoxComplaint.invisibleSlidingBottom()
            return true
        }
        return false
    }

    private fun showBoxDetails(map: GoogleMap, complaint: Complaint) {
        if (complaint.id != complaintSelected) {
            complaintSelected = complaint.id
            map.animateCamera(CameraUpdateFactory.newLatLng(complaint.location!!.toLatLng()))
            populateDetails(complaint)
            if (!rlBoxComplaint.isVisible) {
                rlBoxComplaint.showSlidingTop()
            }
        }
    }

    private fun populateDetails(complaint: Complaint) {
        tvTitle.text = complaint.title
        tvLocation.text = complaint.address
        etDescription.text = complaint.description
        tvCategoryName.text = complaint.category?.name
        tvQtyConfirmations.text = resources.getQuantityString(
                R.plurals.qty_confirmations, complaint.confirmations, complaint.confirmations)
        val remain = complaint.numToBecameCause - complaint.confirmations
        if (remain > 0) {
            tvQtyRemain.text = resources.getQuantityString(R.plurals.qty_remain, remain, remain)
        } else {
            tvQtyRemain.setText(R.string.occurrence_already)
        }
    }

    override fun viewDetails() {
        if (complaintSelected != null) {
            val complaint = complaints!!.first { it.id == complaintSelected }
            val intent = ComplaintDetailsActivity.getIntent(this, complaint)
            startActivityForResult(intent, REQUEST_CONFIRM_REPORT)
        }
    }
    // endregion

    override fun loadComplaints(complaints: List<Complaint>) {
        this.complaints = complaints
        if (map != null) {
            clusterManager.clearItems()
            complaints.forEach {
                if (it.location != null) {
                    clusterManager.addItem(ComplaintItem(it))
                    if (complaintSelected != null && it.id == complaintSelected) {
                        populateDetails(it)
                    }
                }
            }
            clusterManager.cluster()
        }
    }

    private fun updateMap() {
        presenter.list(enterprise!!, category)
        if (lastLatLong != null) {
            map!!.moveCamera(CameraUpdateFactory.newLatLng(lastLatLong!!.toLatLng()))
            lastLatLong = null
        }
    }

    private fun markEnterpriseLocation(map: GoogleMap, enterprise: Enterprise) {
        if (enterprise.location != null) {
            val builder = LatLngBounds.Builder()
            enterprise.location!!.forEach {
                builder.include(it)
            }
            latLngBounds = builder.build()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds!!.center, 14f))
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(): Boolean {
        return if (hasLocationPermission()) {
            map!!.isMyLocationEnabled = true
            locationHelper.requestLocation(this, this)
            true
        } else false
    }

    private fun showAlertCongratulations() {
        AlertDialogFragment
                .create(this) {
                    titleRes = R.string.congratulations
                    messageRes = R.string.successful_contribution
                    multiChoiceItems = arrayOf("Desejo receber notificações sobre a reclamação")
                    onMultiChoiceClickListener { _, _, isChecked ->
                        allowNotifications = isChecked
                    }
                    multiChoiceItemsSelected = arrayListOf(allowNotifications).toBooleanArray()
                    onConfirmClickListener { dialog, _ ->
                        presenter.allowNotification(allowNotifications, insertedComplaintId!!)
                        dialog.dismiss()
                    }
                    onDismissListener {
                        insertedComplaintId = null
                    }
                }
                .show(supportFragmentManager)
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(STATE_COMPLAINTS, ArrayList<Complaint>(complaints))
        outState.putString(STATE_COMPLAINT_SELECTED, complaintSelected)
        outState.putString(STATE_COMPLAINT_INSERTED, insertedComplaintId)
        outState.putBoolean(STATE_UPDATE_MARKERS, updateMarkers)
        outState.putBoolean(STATE_ALLOW_NOTIFICATIONS, allowNotifications)
        outState.putParcelable(STATE_LAST_LAT_LONG, lastLatLong)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        complaints = savedInstanceState.getParcelableArrayList(STATE_COMPLAINTS)
        complaintSelected = savedInstanceState.getString(STATE_COMPLAINT_SELECTED)
        insertedComplaintId = savedInstanceState.getString(STATE_COMPLAINT_INSERTED)
        updateMarkers = savedInstanceState.getBoolean(STATE_UPDATE_MARKERS)
        allowNotifications = savedInstanceState.getBoolean(STATE_ALLOW_NOTIFICATIONS)
        lastLatLong = savedInstanceState.getParcelable(STATE_LAST_LAT_LONG)
    }

    companion object {
        val TAG = ComplaintsActivity::class.simpleName!!

        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val EXTRA_CATEGORIES = "EXTRA_CATEGORIES"
        private val EXTRA_IS_FROM_NOTIFICATION = "EXTRA_IS_FROM_NOTIFICATION"

        private val REQUEST_PERMISSION_LOCATION = 1
        private val REQUEST_CREATE_REPORT = 2
        private val REQUEST_CONFIRM_REPORT = 3

        private val STATE_COMPLAINTS = "STATE_COMPLAINTS"
        private val STATE_COMPLAINT_SELECTED = "STATE_COMPLAINT_SELECTED"
        private val STATE_COMPLAINT_INSERTED = "STATE_COMPLAINT_INSERTED"
        private val STATE_UPDATE_MARKERS = "STATE_UPDATE_MARKERS"
        private val STATE_ALLOW_NOTIFICATIONS = "STATE_ALLOW_NOTIFICATIONS"
        private val STATE_LAST_LAT_LONG = "STATE_LAST_LAT_LONG"

        fun getIntent(context: Context, enterprise: Enterprise, category: Category,
                      isFromNotification: Boolean = false): Intent =
                Intent(context, ComplaintsActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORY, category)
                        .putExtra(EXTRA_IS_FROM_NOTIFICATION, isFromNotification)

        fun getIntent(context: Context, enterprise: Enterprise, categories: ArrayList<Category>): Intent =
                Intent(context, ComplaintsActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORIES, categories)
    }

}
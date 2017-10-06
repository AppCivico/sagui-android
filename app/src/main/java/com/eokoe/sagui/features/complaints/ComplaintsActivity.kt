package com.eokoe.sagui.features.complaints

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.entities.LatLong
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.invisibleSlidingBottom
import com.eokoe.sagui.extensions.isVisible
import com.eokoe.sagui.extensions.setup
import com.eokoe.sagui.extensions.showSlidingTop
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.ViewLocation
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsActivity
import com.eokoe.sagui.features.complaints.report.ReportActivity
import com.eokoe.sagui.utils.BitmapMarker
import com.eokoe.sagui.utils.LocationHelper
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_complaints.*
import kotlinx.android.synthetic.main.content_box_complaint_details.*


/**
 * @author Pedro Silva
 */
class ComplaintsActivity : BaseActivityNavDrawer(), OnMapReadyCallback,
        ComplaintsContract.View, ViewPresenter<ComplaintsContract.Presenter>,
        ViewLocation, LocationHelper.OnLocationReceivedListener {

    override lateinit var presenter: ComplaintsContract.Presenter
    private var map: GoogleMap? = null
    private var showAlertCongratulations = false
    override var locationHelper = LocationHelper()
    private lateinit var mapFragment: SupportMapFragment
    private val markers = ArrayList<Marker>()
    private var complaints: List<Complaint>? = null
    private var complaintSelected: Int = -1
    private var latLngBounds: LatLngBounds? = null
    private var lastLatLong: LatLong? = null
    private var updateMarkers = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaints)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        category = intent.extras?.getParcelable(EXTRA_CATEGORY)
        categories = intent.extras?.getParcelableArrayList(EXTRA_CATEGORIES)
        title = enterprise?.name
        presenter = ComplaintsPresenter(SaguiModelImpl())
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

    override fun onResume() {
        super.onResume()
        navigationView.setCheckedItem(R.id.nav_complaints)
        if (map != null && (lastLatLong != null || updateMarkers)) {
            updateMap()
        }
        if (showAlertCongratulations) {
            showAlertCongratulations()
        }
    }

    private fun updateMap() {
        presenter.list(enterprise!!, category)
        if (lastLatLong != null) {
            map!!.moveCamera(CameraUpdateFactory.newLatLng(lastLatLong!!.toLatLng()))
            lastLatLong = null
        }
    }

    private fun showAlertCongratulations() {
        showAlertCongratulations = false
        AlertDialogFragment
                .create(this) {
                    titleRes = R.string.congratulations
                    messageRes = R.string.successful_contribution
                    multiChoiceItems = arrayOf("Desejo receber notificações sobre a reclamação")
                }
                .show(supportFragmentManager)
    }

    override fun onBackPressed() {
        if (!hideBoxDetails()) {
            super.onBackPressed()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setup(this)
        markEnterpriseLocation(map, enterprise!!)
        presenter.list(enterprise!!, category)
        if (!requestLocation()) {
            requestLocationPermission(R.string.title_request_location_permission,
                    R.string.message_request_location_permission, REQUEST_PERMISSION_LOCATION)
        }
        map.setOnMarkerClickListener { marker ->
            val index = markers.indexOf(marker)
            if (index >= 0 && complaints!!.size > index) {
                showBoxDetails(index, map, marker)
                true
            } else {
                false
            }
        }
        map.setOnMapClickListener {
            hideBoxDetails()
        }
    }

    private fun hideBoxDetails(): Boolean {
        complaintSelected = -1
        if (rlBoxComplaint.isVisible) {
            rlBoxComplaint.invisibleSlidingBottom()
            return true
        }
        return false
    }

    private fun showBoxDetails(index: Int, map: GoogleMap, marker: Marker) {
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
        if (index != complaintSelected) {
            complaintSelected = index
            populateDetails(index)
            if (!rlBoxComplaint.isVisible) {
                rlBoxComplaint.showSlidingTop()
            }
        }
    }

    private fun populateDetails(index: Int) {
        if (index > -1 && index < complaints!!.size) {
            val complaint = complaints!![index]
            tvTitle.text = complaint.title
            tvLocation.text = complaint.address
            tvDescription.text = complaint.description
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
    }

    override fun onLocationReceived(location: Location) {
        cameraToCurrentLocation(map!!, location)
    }

    override fun loadComplaints(complaints: List<Complaint>) {
        this.complaints = complaints
        if (map != null) {
            markers.forEach { it.remove() }
            markers.clear()
            complaints.forEach {
                val latLng = LatLng(it.location!!.latitude, it.location!!.longitude)
                val bm = BitmapMarker.build(this) {
                    color = ContextCompat.getColor(this@ComplaintsActivity,
                            if (it.isCause) R.color.markerCauseColor
                            else R.color.markerComplaintColor
                    )
                    textColor = Color.WHITE
                    radiusDP = 5f
                    text = if (it.confirmations < 100) {
                        "${it.confirmations}"
                    } else {
                        "99+"
                    }
                }
                val marker = MarkerOptions()
                        .icon(bm.icon)
                        .position(latLng)
                        .anchor(bm.anchorPoints[0], bm.anchorPoints[1])
                markers.add(map!!.addMarker(marker))
            }
            populateDetails(complaintSelected)
        }
    }

    override fun viewDetails() {
        if (complaintSelected > -1) {
            val intent = ComplaintDetailsActivity.getIntent(this, complaints!![complaintSelected])
            startActivityForResult(intent, REQUEST_CONFIRM_REPORT)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(): Boolean {
        if (hasLocationPermission()) {
            map!!.isMyLocationEnabled = true
            locationHelper.requestLocation(this, this)
            return true
        }
        return false
    }

    private fun cameraToCurrentLocation(map: GoogleMap, location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (latLngBounds?.contains(latLng) == true) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun markEnterpriseLocation(map: GoogleMap, enterprise: Enterprise) {
        if (enterprise.location != null) {
            val polygonOptions = PolygonOptions()
                    .addAll(enterprise.location!!)
                    .strokeColor(ContextCompat.getColor(this, R.color.mapFillColor))
                    .strokeWidth(2f)

            map.addPolygon(polygonOptions)
            val builder = LatLngBounds.Builder()
            enterprise.location!!.forEach {
                builder.include(it)
            }
            latLngBounds = builder.build()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds!!.center, 14f))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            requestLocation()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CREATE_REPORT) {
            if (resultCode == Activity.RESULT_OK) {
                showAlertCongratulations = true
                lastLatLong = data?.getParcelableExtra(ReportActivity.RESULT_LAT_LONG)
            }
        } else if (requestCode == REQUEST_CONFIRM_REPORT) {
            updateMarkers = resultCode == Activity.RESULT_OK
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val EXTRA_CATEGORIES = "EXTRA_CATEGORIES"
        private val REQUEST_PERMISSION_LOCATION = 1
        private val REQUEST_CREATE_REPORT = 2
        private val REQUEST_CONFIRM_REPORT = 3

        fun getIntent(context: Context, enterprise: Enterprise, category: Category): Intent =
                Intent(context, ComplaintsActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORY, category)

        fun getIntent(context: Context, enterprise: Enterprise, categories: ArrayList<Category>): Intent =
                Intent(context, ComplaintsActivity::class.java)
                        .putExtra(EXTRA_ENTERPRISE, enterprise)
                        .putExtra(EXTRA_CATEGORIES, categories)
    }
}
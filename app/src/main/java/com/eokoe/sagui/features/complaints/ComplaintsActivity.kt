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
    private var category: Category? = null
    private var map: GoogleMap? = null
    private var showAlertCongratulations = false
    override var locationHelper = LocationHelper()
    lateinit var mapFragment: SupportMapFragment
    private val markers = ArrayList<Marker>()
    private var complaints: List<Complaint>? = null
    private var complaintSelected: Int = -1
    var latLngBounds: LatLngBounds? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaints)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        category = intent.extras?.getParcelable(EXTRA_CATEGORY)
        title = enterprise?.name
        presenter = ComplaintsPresenter(SaguiModelImpl())
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    override fun init(savedInstanceState: Bundle?) {
        mapFragment.getMapAsync(this)
        fabAdd.setOnClickListener {
            startActivityForResult(ReportActivity.getIntent(this, enterprise!!, category), REQUEST_CREATE_REPORT)
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
        if (showAlertCongratulations) {
            showAlertCongratulations = false
            AlertDialogFragment
                    .create(this) {
                        titleRes = R.string.congratulations
                        messageRes = R.string.successful_contribution
                        multiChoiceItems = arrayOf("Desejo receber notificações sobre a reclamação")
                    }
                    .show(supportFragmentManager)
            mapFragment.getMapAsync(this)
        }
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
            val complaint = complaints!![index]
            tvTitle.text = complaint.title
            tvLocation.text = complaint.address
            tvDescription.text = complaint.description
            tvCategoryName.text = complaint.category?.name
            tvQtyComplaints.text = resources.getQuantityString(
                    R.plurals.qty_confirmations, complaint.confirmations, complaint.confirmations)
            val remain = MAX_COUNT_CONFIRMATION - complaint.confirmations
            if (remain > 0) {
                tvQtyRemain.text = resources.getQuantityString(R.plurals.qty_remain, remain, remain)
            } else {
                tvQtyRemain.setText(R.string.occurrence_already)
            }
            if (!rlBoxComplaint.isVisible) {
                rlBoxComplaint.showSlidingTop()
            }
        }
    }

    override fun onLocationReceived(location: Location) {
        cameraToCurrentLocation(map!!, location)
    }

    override fun loadComplaints(complaints: List<Complaint>) {
        this.complaints = complaints
        markers.forEach { it.remove() }
        markers.clear()
        complaints.forEach {
            val latLng = LatLng(it.location!!.latitude, it.location!!.longitude)
            val bm = BitmapMarker.build(this) {
                color = Color.parseColor("#D22F33")
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
    }

    override fun viewDetails() {
        if (complaintSelected > -1) {
            startActivity(ComplaintDetailsActivity.getIntent(this, complaints!![complaintSelected]))
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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds!!.center, 14f))
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
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val REQUEST_PERMISSION_LOCATION = 1
        private val REQUEST_CREATE_REPORT = 2
        private val MAX_COUNT_CONFIRMATION = 30

        fun getIntent(context: Context, enterprise: Enterprise, category: Category? = null): Intent {
            val intent = Intent(context, ComplaintsActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            intent.putExtra(EXTRA_CATEGORY, category)
            return intent
        }
    }
}
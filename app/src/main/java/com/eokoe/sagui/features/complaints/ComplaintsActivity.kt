package com.eokoe.sagui.features.complaints

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.ViewLocation
import com.eokoe.sagui.features.complaints.report.ReportActivity
import com.eokoe.sagui.utils.LocationHelper
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_complaints.*


/**
 * @author Pedro Silva
 */
class ComplaintsActivity : BaseActivityNavDrawer(), OnMapReadyCallback,
        ViewLocation, LocationHelper.OnLocationReceivedListener {

    private var category: Category? = null
    private var map: GoogleMap? = null
    private var showAlertCongratulations = false
    override var locationHelper = LocationHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaints)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        category = intent.extras?.getParcelable(EXTRA_CATEGORY)
        title = enterprise?.name
    }

    override fun init(savedInstanceState: Bundle?) {
        fabAdd.setOnClickListener {
            startActivityForResult(ReportActivity.getIntent(this), REQUEST_CREATE_REPORT)
        }
    }

    override fun onResume() {
        super.onResume()
        navigationView.setCheckedItem(R.id.nav_complaints)
        if (showAlertCongratulations) {
            showAlertCongratulations = false
            AlertDialogFragment.newInstance(this, R.string.congratulations, R.string.successful_contribution)
                    .show(supportFragmentManager)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        if (!requestLocation()) {
            requestLocationPermission(R.string.title_request_location_permission,
                    R.string.message_request_location_permission, REQUEST_PERMISSION_LOCATION)
        }
    }

    override fun onLocationReceived(location: Location) {
        cameraToCurrentLocation(map!!, location)
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
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

        fun getIntent(context: Context, enterprise: Enterprise): Intent {
            val intent = Intent(context, ComplaintsActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            return intent
        }

        fun getIntent(context: Context, enterprise: Enterprise, category: Category): Intent {
            val intent = getIntent(context, enterprise)
            intent.putExtra(EXTRA_CATEGORY, category)
            return intent
        }
    }
}
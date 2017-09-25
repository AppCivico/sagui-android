package com.eokoe.sagui.features.complaints

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.utils.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions



/**
 * @author Pedro Silva
 */
class ComplaintsActivity : BaseActivityNavDrawer(), OnMapReadyCallback, LocationHelper.OnLocationReceivedListener {

    private val REQUEST_PERMISSION_LOCATION = 2

    private var category: Category? = null
    private val locationHelper = LocationHelper()
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaints)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        locationHelper.start()
    }

    override fun onStop() {
        locationHelper.stop()
        super.onStop()
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprise = intent.extras?.getParcelable(EXTRA_ENTERPRISE)
        category = intent.extras?.getParcelable(EXTRA_CATEGORY)
        title = enterprise?.name
    }

    override fun init(savedInstanceState: Bundle?) {
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        if (hasLocationPermission()) {
            requestLocation()
        } else {
            requestLocationPermission(REQUEST_PERMISSION_LOCATION)
        }
    }

    override fun onLocationReceived(location: Location) {
        cameraToCurrentLocation(map!!, location)
    }

    @SuppressLint("MissingPermission")
    fun requestLocation() {
        map!!.isMyLocationEnabled = true
        locationHelper.requestLocation(this, this)
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

    companion object {
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"

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
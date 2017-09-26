package com.eokoe.sagui.features.complaints.report.pin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewLocation
import com.eokoe.sagui.utils.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 * @author Pedro Silva
 * @since 26/09/17
 */
class PinActivity : BaseActivity(), OnMapReadyCallback,
        ViewLocation, LocationHelper.OnLocationReceivedListener {

    override var locationHelper = LocationHelper()
    private var map: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        if (!requestLocation()) {
            requestLocationPermission(R.string.title_request_location_permission,
                    R.string.message_request_location_permission, REQUEST_PERMISSION_LOCATION)
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

    override fun onLocationReceived(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.zIndex(1f)
        marker = map!!.addMarker(markerOptions)
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        map!!.setOnCameraMoveListener {
            marker?.position = map!!.cameraPosition.target
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            requestLocation()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_ok) {
            val latLng = LatLng(marker!!.position.latitude, marker!!.position.longitude)
            intent.putExtra(EXTRA_LOCATION, latLng)
            setResult(Activity.RESULT_OK, intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val REQUEST_PERMISSION_LOCATION = 1
        public val EXTRA_LOCATION = "EXTRA_LOCATION"

        fun getIntent(context: Context) = Intent(context, PinActivity::class.java)
    }
}
package com.eokoe.sagui.features.complaints.report.pin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.LatLong
import com.eokoe.sagui.extensions.hideSlidingBottom
import com.eokoe.sagui.extensions.isVisible
import com.eokoe.sagui.extensions.setup
import com.eokoe.sagui.extensions.showSlidingTop
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewLocation
import com.eokoe.sagui.services.FetchAddressIntentService
import com.eokoe.sagui.utils.BitmapMarker
import com.eokoe.sagui.utils.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_pin.*
import java.util.concurrent.TimeUnit

/**
 * @author Pedro Silva
 * @since 26/09/17
 */
class PinActivity : BaseActivity(), OnMapReadyCallback,
        ViewLocation, LocationHelper.OnLocationReceivedListener {

    override var locationHelper = LocationHelper()
    private var map: GoogleMap? = null
    private var latLong: LatLong? = null
    private lateinit var receiver: ResultReceiver
    private var address: String? = null

    private val locationChangeSubject = PublishSubject.create<LatLng>()
    private val addressChangeSubject = PublishSubject.create<String>()
    private val queueCountSubject = PublishSubject.create<Int>()
    private var queueCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
        latLong = intent?.extras?.getParcelable(EXTRA_LAT_LNG)
        address = intent?.extras?.getString(EXTRA_ADDRESS)
    }

    override fun init(savedInstanceState: Bundle?) {
        receiver = AddressResultReceiver(Handler())
        locationChangeSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { return@map LatLong(it.latitude, it.longitude) }
                .subscribe {
                    latLong = it
                    if (Geocoder.isPresent()) {
                        hideBox()
                        startService(FetchAddressIntentService.getIntent(this, receiver, it))
                    } else {
                        Toast.makeText(this, "no_geocoder_available", Toast.LENGTH_SHORT).show()
                    }
                }

        addressChangeSubject
                .subscribe({
                    address = it
                    tvAddress.text = address
                    showBox()
                }, {
                    queueCount--
                    Toast.makeText(this@PinActivity, it.message, Toast.LENGTH_SHORT).show()
                })

        queueCountSubject
                .map {
                    queueCount += it
                    return@map queueCount
                }.filter { return@filter it <= 0 }
                .subscribe {
                    queueCount = 0
                    llBoxAddress.showSlidingTop()
                }

        btnConfirm.setOnClickListener { confirmLocation() }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setup(this)
        if (latLong != null) {
            setupMarker(latLong!!.latitude, latLong!!.longitude)
        }
        if (!requestLocation()) {
            requestLocationPermission(R.string.title_request_location_permission,
                    R.string.message_request_location_permission, REQUEST_PERMISSION_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(): Boolean {
        if (hasLocationPermission()) {
            map!!.isMyLocationEnabled = true
            if (latLong == null) {
                locationHelper.requestLocation(this, this)
            }
            return true
        }
        return false
    }

    override fun onLocationReceived(location: Location) {
        setupMarker(location.latitude, location.longitude)
    }

    private fun setupMarker(latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
                .position(latLng)
        val marker = map!!.addMarker(markerOptions)
        if (address == null) {
            locationChangeSubject.onNext(latLng)
        } else {
            addressChangeSubject.onNext(address!!)
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        map!!.setOnCameraMoveStartedListener {
            if (llBoxAddress.isVisible()) {
                llBoxAddress.hideSlidingBottom()
            }
        }
        map!!.setOnCameraMoveListener {
            marker.position = map!!.cameraPosition.target
            locationChangeSubject.onNext(marker.position)
        }
        map!!.setOnMapLongClickListener {
            map!!.animateCamera(CameraUpdateFactory.newLatLng(it))
        }
    }

    fun hideBox() {
        queueCountSubject.onNext(1)
    }

    fun showBox() {
        queueCountSubject.onNext(-1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            requestLocation()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun confirmLocation() {
        val intent = Intent()
        intent.putExtra(RESULT_LOCATION, latLong)
        intent.putExtra(RESULT_ADDRESS, address)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        private val REQUEST_PERMISSION_LOCATION = 1
        val EXTRA_LAT_LNG = "EXTRA_LAT_LNG"
        val EXTRA_ADDRESS = "EXTRA_ADDRESS"
        val RESULT_LOCATION = "RESULT_LOCATION"
        val RESULT_ADDRESS = "RESULT_ADDRESS"

        fun getIntent(context: Context, latLong: LatLong? = null, address: String? = null): Intent {
            val intent = Intent(context, PinActivity::class.java)
            intent.putExtra(EXTRA_LAT_LNG, latLong)
            intent.putExtra(EXTRA_ADDRESS, address)
            return intent
        }
    }

    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            val result = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY)
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
                addressChangeSubject.onNext(result)
            } else {
                addressChangeSubject.onError(Exception(result))
            }
        }
    }
}
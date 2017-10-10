package com.eokoe.sagui.features.complaints.report.pin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.entities.LatLong
import com.eokoe.sagui.data.exceptions.SaguiException
import com.eokoe.sagui.extensions.hideSlidingBottom
import com.eokoe.sagui.extensions.isVisible
import com.eokoe.sagui.extensions.setup
import com.eokoe.sagui.extensions.showSlidingTop
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewLocation
import com.eokoe.sagui.services.FetchAddressService
import com.eokoe.sagui.utils.LocationHelper
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
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
    private var address: String? = null
    private var enterprise: Enterprise? = null

    private val locationChangeSubject = PublishSubject.create<LatLng>()
    private val addressChangeSubject = PublishSubject.create<String>()
    private val queueCountSubject = PublishSubject.create<Int>()
    private var queueCount = 0
    private var latLngBounds: LatLngBounds? = null
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
        latLong = intent?.extras?.getParcelable(EXTRA_LAT_LNG)
        address = intent?.extras?.getString(EXTRA_ADDRESS)
        enterprise = intent?.extras?.getParcelable(EXTRA_ENTERPRISE)
        RxJavaPlugins.setErrorHandler {
            if (it.message == "no_address_found") {
                showBox()
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        queueCountSubject
                .map {
                    queueCount += it
                    return@map queueCount
                }.filter { return@filter it >= 0 }
                .subscribe {
                    queueCount = 0
                    llBoxAddress.showSlidingTop()
                }
        addressChangeSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    address = it
                    tvAddress.text = address
                    showBox()
                }
        addressChangeSubject.onNext("OPA!")
        locationChangeSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { return@map LatLong(it.latitude, it.longitude) }
                .subscribe({
                    latLong = it
                    if (Geocoder.isPresent()) {
                        hideBox()
                        startService(FetchAddressService.getIntent(this, AddressResultReceiver(Handler()), it))
                    } else {
                        Toast.makeText(this, "no_geocoder_available", Toast.LENGTH_SHORT).show()
                    }
                }, {
                    Toast.makeText(this@PinActivity, it.message, Toast.LENGTH_SHORT).show()
                })

        btnConfirm.setOnClickListener { confirmLocation() }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setup(this)

        map.setOnCameraMoveStartedListener {
            if (llBoxAddress.isVisible) {
                llBoxAddress.hideSlidingBottom()
            }
        }
        map.setOnCameraMoveListener {
            val latLng = map.cameraPosition.target
            marker?.position = latLng
            locationChangeSubject.onNext(latLng)
        }
        markEnterpriseLocation(map, enterprise!!)
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
        var latLng = LatLng(latitude, longitude)
        if (!latLngBounds!!.contains(latLng)) {
            latLng = latLngBounds!!.center
        }
        if (marker == null) {
            val markerOptions = MarkerOptions().position(latLng)
            marker = map!!.addMarker(markerOptions)
        } else {
            marker!!.position = latLng
        }
        if (address == null) {
            locationChangeSubject.onNext(latLng)
        } else {
            addressChangeSubject.onNext(address!!)
        }
        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun markEnterpriseLocation(map: GoogleMap, enterprise: Enterprise) {
        if (enterprise.location != null) {
            val polygonOptions = PolygonOptions()
                    .addAll(enterprise.location!!)
                    .strokeColor(ContextCompat.getColor(this, R.color.strokePolygonMap))
                    .strokeWidth(2f)
                    .fillColor(ContextCompat.getColor(this, R.color.mapFillColor))
            map.addPolygon(polygonOptions)
            val builder = LatLngBounds.Builder()
            enterprise.location!!.forEach {
                builder.include(it)
            }
            latLngBounds = builder.build()
            if (latLong == null) {
                setupMarker(latLngBounds!!.center.latitude, latLngBounds!!.center.longitude)
            }
        }
    }

    private fun hideBox() {
        queueCountSubject.onNext(-1)
    }

    private fun showBox() {
        queueCountSubject.onNext(1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            requestLocation()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun confirmLocation() {
        if (latLong != null
                && latLngBounds?.contains(LatLng(latLong!!.latitude, latLong!!.longitude)) == true) {
            val intent = Intent()
            intent.putExtra(RESULT_LOCATION, latLong)
            intent.putExtra(RESULT_ADDRESS, address)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            AlertDialogFragment.create(this) {
                title = "Local não disponível"
                message = "O local selecionado está fora da cobertura do empreendimento.\nPor favor, selecione outra localização."
            }.show(supportFragmentManager)
        }
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putParcelable(STATE_LAT_LONG, latLong)
        outState.putString(STATE_ADDRESS, address)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        latLong = savedInstanceState.getParcelable(STATE_LAT_LONG)
        address = savedInstanceState.getString(STATE_ADDRESS)
    }

    companion object {
        private val REQUEST_PERMISSION_LOCATION = 1
        val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        val EXTRA_LAT_LNG = "EXTRA_LAT_LNG"
        val EXTRA_ADDRESS = "EXTRA_ADDRESS"
        val RESULT_LOCATION = "RESULT_LOCATION"
        val RESULT_ADDRESS = "RESULT_ADDRESS"

        private val STATE_LAT_LONG = "STATE_LAT_LONG"
        private val STATE_ADDRESS = "STATE_ADDRESS"

        fun getIntent(context: Context, enterprise: Enterprise, latLong: LatLong? = null, address: String? = null): Intent {
            val intent = Intent(context, PinActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            intent.putExtra(EXTRA_LAT_LNG, latLong)
            intent.putExtra(EXTRA_ADDRESS, address)
            return intent
        }
    }

    @SuppressLint("ParcelCreator")
    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            val result = resultData.getString(FetchAddressService.RESULT_DATA_KEY)
            if (resultCode == FetchAddressService.SUCCESS_RESULT) {
                addressChangeSubject.onNext(result)
            } else {
                addressChangeSubject.onError(SaguiException(result))
            }
        }
    }
}
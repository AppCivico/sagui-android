package com.eokoe.sagui.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes


/**
 * @author Pedro Silva
 * @since 15/09/17
 */
class LocationHelper : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var listener: OnLocationReceivedListener? = null
    private var resolvingGooglePlayError = false
    private lateinit var activity: Activity

    fun requestLocation(activity: Activity, listener: OnLocationReceivedListener) {
        this.activity = activity
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(activity.applicationContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
            start()
        }
        this.listener = listener
    }

    fun start() {
        googleApiClient?.connect()
    }

    fun stop() {
        googleApiClient?.disconnect()
    }

    fun disconnect() {
        stop()
        googleApiClient = null
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(listener: OnLocationReceivedListener) {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, LocationListener(listener))

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        status.startResolutionForResult(activity, REQUEST_GOOGLE_PLAY_RESOLVE_ERROR)
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }

    fun onActivityResult(resultCode: Int) {
        resolvingGooglePlayError = false
        if (resultCode == Activity.RESULT_OK && googleApiClient != null
                && googleApiClient!!.isConnecting && googleApiClient!!.isConnected) {
            googleApiClient!!.connect()
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        if (!resolvingGooglePlayError) {
            if (result.hasResolution()) {
                try {
                    resolvingGooglePlayError = true
                    result.startResolutionForResult(activity, REQUEST_GOOGLE_PLAY_RESOLVE_ERROR)
                } catch (error: IntentSender.SendIntentException) {
                    googleApiClient?.connect()
                }
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        getLocation(listener!!)
    }

    override fun onConnectionSuspended(i: Int) {
    }

    companion object {
        val REQUEST_GOOGLE_PLAY_RESOLVE_ERROR = 1001
    }

    inner class LocationListener(private val listener: OnLocationReceivedListener) : com.google.android.gms.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            listener.onLocationReceived(location)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            disconnect()
        }
    }

    interface OnLocationReceivedListener {
        fun onLocationReceived(location: Location)
    }
}
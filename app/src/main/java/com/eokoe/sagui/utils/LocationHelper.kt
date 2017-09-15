package com.eokoe.sagui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

/**
 * @author Pedro Silva
 * @since 15/09/17
 */
class LocationHelper : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var listener: OnLocationReceivedListener? = null
    private var resolvingGooglePlayError = false
    private var activity: Activity? = null
    private var requestCodeGooglePlayResolveError: Int? = null

    fun requestLocation(context: Context, listener: OnLocationReceivedListener) {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context)
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

    private fun getLocation(listener: OnLocationReceivedListener) {
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (location != null) {
            listener.onLocationReceived(location)
        } else {
            val locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, LocationListener(listener))
        }
    }

    fun registerOnConnectionFailed(activity: Activity, requestCode: Int) {
        this.activity = activity
        this.requestCodeGooglePlayResolveError = requestCode
    }

    fun onActivityResult(resultCode: Int, data: Intent?) {
        resolvingGooglePlayError = false
        if (resultCode == Activity.RESULT_OK && googleApiClient != null
                && googleApiClient!!.isConnecting && googleApiClient!!.isConnected) {
            googleApiClient!!.connect()
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        if (!resolvingGooglePlayError && activity != null && requestCodeGooglePlayResolveError != null) {
            if (result.hasResolution()) {
                try {
                    resolvingGooglePlayError = true
                    result.startResolutionForResult(activity, requestCodeGooglePlayResolveError!!)
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
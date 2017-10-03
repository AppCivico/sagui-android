package com.eokoe.sagui.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import com.eokoe.sagui.data.entities.LatLong
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Pedro Silva
 */
class FetchAddressService : IntentService(TAG) {
    var receiver: ResultReceiver? = null

    override fun onHandleIntent(intent: Intent) {
        var errorMessage = ""
        val location = intent.getParcelableExtra<LatLong>(LOCATION_DATA_EXTRA)
        var addresses: List<Address>? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        receiver = intent.getParcelableExtra(RECEIVER)

        try {
            addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1)
        } catch (error: IOException) {
            // TODO FIX message
            errorMessage = "service_not_available"
        } catch (error: IllegalArgumentException) {
            // TODO FIX message
            errorMessage = "invalid_lat_long_used"
        }
        if (addresses?.size ?: 0 == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no_address_found"
            }
            deliverResultToReceiver(FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses!![0]
            val addressFragments = ArrayList<String>()
            (0..address.maxAddressLineIndex)
                    .mapTo(addressFragments) {
                        address.getAddressLine(it)
                    }
            deliverResultToReceiver(SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments))
        }
    }

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle()
        bundle.putString(RESULT_DATA_KEY, message)
        receiver?.send(resultCode, bundle)
    }

    companion object {
        val TAG = FetchAddressService::class.simpleName
        val RESULT_DATA_KEY = "RESULT_DATA_KEY"
        private val LOCATION_DATA_EXTRA = "LOCATION_DATA_EXTRA"
        private val RECEIVER = "RECEIVER"
        val SUCCESS_RESULT = 0
        val FAILURE_RESULT = 1

        fun getIntent(context: Context, receiver: ResultReceiver, location: LatLong): Intent {
            val intent = Intent(context, FetchAddressService::class.java)
            intent.putExtra(RECEIVER, receiver)
            intent.putExtra(LOCATION_DATA_EXTRA, location)
            intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING)
            return intent
        }
    }
}
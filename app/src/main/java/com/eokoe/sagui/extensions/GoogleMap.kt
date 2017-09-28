package com.eokoe.sagui.extensions

import android.content.Context
import android.content.res.Resources
import com.eokoe.sagui.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions


/**
 * @author Pedro Silva
 * @since 27/09/17
 */
fun GoogleMap.setup(context: Context): Boolean {
    return try {
        uiSettings.isMapToolbarEnabled = false
        setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.maps_style_json))
    } catch (e: Resources.NotFoundException) {
        false
    }
}
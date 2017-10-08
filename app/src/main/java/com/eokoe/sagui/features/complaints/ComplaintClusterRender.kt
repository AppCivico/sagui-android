package com.eokoe.sagui.features.complaints

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import com.eokoe.sagui.R
import com.eokoe.sagui.utils.BitmapMarker
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ComplaintClusterRender(map: GoogleMap, val context: Context, clusterManager: ClusterManager<ComplaintItem>)
    : DefaultClusterRenderer<ComplaintItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ComplaintItem, markerOptions: MarkerOptions) {
        val bm = BitmapMarker.build(context) {
            color = ContextCompat.getColor(context,
                    if (item.complaint.isCause) R.color.markerCauseColor
                    else R.color.markerComplaintColor
            )
            textColor = Color.WHITE
            radiusDP = 5f
            text = if (item.complaint.confirmations < 100) "${item.complaint.confirmations}"
                else "99+"
        }
        markerOptions.icon(bm.icon)
        markerOptions.anchor(bm.anchorPoints[0], bm.anchorPoints[1])
    }
}
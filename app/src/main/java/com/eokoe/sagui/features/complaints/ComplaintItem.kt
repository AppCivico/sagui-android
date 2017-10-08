package com.eokoe.sagui.features.complaints

import com.eokoe.sagui.data.entities.Complaint
import com.google.maps.android.clustering.ClusterItem

class ComplaintItem(val complaint: Complaint) : ClusterItem {
    override fun getSnippet(): String? = null

    override fun getTitle(): String? = null

    override fun getPosition() = complaint.location!!.toLatLng()
}
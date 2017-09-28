package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
@PaperParcel
open class Complaint(
        var id: String? = null,
        var title: String = "",
        var description: String = "",
        @SerializedName("axis_id")
        var categoryId: String? = null,
        var location: LatLong? = null,
        @SerializedName("human_address")
        var address: String? = null,
        @SerializedName("enterprise_id")
        var enterpriseId: String? = null,
        var confirmations: Int = 0
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelComplaint.CREATOR
    }
}
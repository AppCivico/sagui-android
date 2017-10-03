package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
@PaperParcel
@Suppress("ArrayInDataClass")
open class Location(
        @Expose
        @SerializedName("geo")
        open var latLong: LatLong = LatLong(),

        @Expose
        @SerializedName("human_address")
        open var location: String = ""
) : PaperParcelable, RealmObject() {

    companion object {
        @JvmField
        val CREATOR = PaperParcelLocation.CREATOR
    }
}
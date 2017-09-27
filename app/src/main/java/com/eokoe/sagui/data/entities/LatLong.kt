package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
@PaperParcel
open class LatLong(
        @SerializedName("lat")
        open var latitude: Double = 0.0,
        @SerializedName("long")
        open var longitude: Double = 0.0
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelLatLong.CREATOR
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LatLong

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "LatLong(latitude=$latitude, longitude=$longitude)"
    }
}
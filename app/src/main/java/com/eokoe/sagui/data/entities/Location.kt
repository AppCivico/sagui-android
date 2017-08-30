package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
@PaperParcel
@Suppress("ArrayInDataClass")
data class Location(
        @SerializedName("geo")
        val latLong: DoubleArray,
        @SerializedName("human_address")
        val location: String
): PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelLocation.CREATOR
    }
}
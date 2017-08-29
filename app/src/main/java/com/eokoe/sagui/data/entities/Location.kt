package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
@Suppress("ArrayInDataClass")
data class Location(
        @SerializedName("geo")
        val latLong: DoubleArray,
        @SerializedName("human_address")
        val location: String
)
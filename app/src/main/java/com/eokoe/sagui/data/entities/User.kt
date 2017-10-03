package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Pedro Silva
 */
data class User(
        @Expose
        @SerializedName("api_key")
        val apiKey: String = ""
)
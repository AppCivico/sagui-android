package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName

/**
 * @author Pedro Silva
 */
data class User(
        @SerializedName("api_key")
        val apiKey: String = ""
)
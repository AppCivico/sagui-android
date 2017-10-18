package com.eokoe.sagui.data.net.auth

import android.content.Context
import com.eokoe.sagui.data.entities.Device
import com.eokoe.sagui.data.entities.User
import com.eokoe.sagui.data.net.RequestInterceptor
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.*


/**
 * @author Pedro Silva
 */
class ApiKeyManagerImpl(
        val context: Context,
        private val baseUrl: String
) : ApiKeyManager {

    override val apiKey: String
        get() = CredentialValues.API_KEY.getString(context)

    override val hasApiKey: Boolean
        get() = apiKey.isNotEmpty()

    override val deviceKey: String
        get() = CredentialValues.DEVICE_KEY.getString(context)

    override fun clearApiKey() {
        CredentialValues.API_KEY.remove(context)
    }

    override fun newApiKey(): String {
        var accessToken = ""
        val gson = Gson()
        if (deviceKey.isEmpty()) {
            CredentialValues.DEVICE_KEY.putString(context, UUID.randomUUID().toString())
        }
        val requestBody = RequestBody.create(
                MediaType.parse("application/json"), gson.toJson(Device(deviceKey)))
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(baseUrl + "auth/device")
                .header("User-Agent", RequestInterceptor.USER_AGENT)
                .method("POST", requestBody)
                .build()

        try {
            val response = client.newCall(request).execute()
            if (response.code() == 200) {
                val jsonData = response.body()?.string()
                if (jsonData != null) {
                    val user = gson.fromJson(jsonData, User::class.java)
                    CredentialValues.API_KEY.putString(context, user.apiKey)
                    accessToken = user.apiKey
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return accessToken
    }
}
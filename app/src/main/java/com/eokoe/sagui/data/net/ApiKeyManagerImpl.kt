package com.eokoe.sagui.data.net

import android.content.Context
import com.eokoe.sagui.data.entities.Device
import com.eokoe.sagui.data.entities.User
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
    override fun getApiKey(): String = CredentialValues.API_KEY.getString(context)

    override fun hasApiKey() = getApiKey().isNotEmpty()

    override fun clearApiKey() {
        CredentialValues.API_KEY.remove(context)
    }

    override fun newApiKey(): String? {
        var accessToken: String? = null
        val gson = Gson()

        var deviceKey = CredentialValues.DEVICE_KEY.getString(context)
        if (deviceKey.isEmpty()) {
            deviceKey = UUID.randomUUID().toString()
            CredentialValues.DEVICE_KEY.putString(context, deviceKey)
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
package com.eokoe.sagui.data.net.auth

import android.content.Context

/**
 * @author Pedro Silva
 */
enum class CredentialValues {
    DEVICE_KEY, API_KEY;

    private val preferencesName = "CredentialValues"

    private fun getPreferences(context: Context) =
            context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    fun getString(context: Context) =
            getPreferences(context).getString(name, "") ?: ""

    fun putString(context: Context, value: String) {
        getPreferences(context).edit().putString(name, value).apply()
    }

    fun remove(context: Context) {
        getPreferences(context).edit().remove(name).apply()
    }
}
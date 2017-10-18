package com.eokoe.sagui.data.net.auth

/**
 * @author Pedro Silva
 */
interface ApiKeyManager {
    val apiKey: String
    val deviceKey: String
    val hasApiKey: Boolean
    fun clearApiKey()
    fun newApiKey(): String
}
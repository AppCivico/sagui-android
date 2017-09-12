package com.eokoe.sagui.data.net

/**
 * @author Pedro Silva
 */
interface ApiKeyManager {
    fun getApiKey(): String
    fun hasApiKey(): Boolean
    fun clearApiKey()
    fun newApiKey(): String?
}
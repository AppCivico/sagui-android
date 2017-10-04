package com.eokoe.sagui.data.net.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * @author Pedro Silva
 * @since 12/09/17
 */
class ApiKeyAuthenticator(private val apiKeyManager: ApiKeyManager): Authenticator {
    override fun authenticate(route: Route, response: Response): Request {
        val apiKey = apiKeyManager.newApiKey()
        return response.request().newBuilder()
                .header("X-API-KEY", apiKey)
                .build()
    }
}
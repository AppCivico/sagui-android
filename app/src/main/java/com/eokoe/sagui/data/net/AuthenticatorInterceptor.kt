package com.eokoe.sagui.data.net

import okhttp3.Interceptor
import okhttp3.Response


/**
 * @author Pedro Silva
 * @since 12/09/17
 */
class AuthenticatorInterceptor(
        private val apiKeyManager: ApiKeyManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authenticationRequest = originalRequest.newBuilder().build()
        val origResponse = chain.proceed(authenticationRequest)

        return if (origResponse.code() == 401 || origResponse.code() == 403) {
            val apiKey = apiKeyManager.newApiKey()
            val newAuthenticationRequest = originalRequest.newBuilder()
                    .header("X-API-KEY", apiKey)
                    .build()
            chain.proceed(newAuthenticationRequest)
        } else {
            origResponse
        }
    }

}
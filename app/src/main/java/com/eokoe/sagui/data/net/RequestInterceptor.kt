package com.eokoe.sagui.data.net

import android.os.Build
import com.eokoe.sagui.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * @author Pedro Silva
 * @since 20/07/17
 */

class RequestInterceptor(
        private val apiKeyManager: ApiKeyManager
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
                .header("User-Agent", USER_AGENT)
                .header("Charset", "UTF-8")
                .method(original.method(), original.body())

        if (apiKeyManager.hasApiKey()) {
            request.header("X-API-KEY", apiKeyManager.getApiKey())
        }
        return chain.proceed(request.build())
    }

    companion object {
        val USER_AGENT: String
            get() = String.format(
                    "Android %s/Model %s %s/Sagui %s/Time %s %s",
                    Build.VERSION.RELEASE,
                    Build.BRAND,
                    Build.MODEL,
                    BuildConfig.VERSION_NAME,
                    System.currentTimeMillis(),
                    Calendar.getInstance().timeZone.id
            )
    }
}

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

class RequestInterceptor internal constructor() : Interceptor {
    private var USER_AGENT = "Android " + Build.VERSION.RELEASE +
            "/Model " + Build.BRAND + " " + Build.MODEL +
            "/SFH " + BuildConfig.VERSION_NAME +
            "/Time %s %s"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val timezone = Calendar.getInstance().timeZone.id
        val request = original.newBuilder()
                .header("User-Agent", String.format(USER_AGENT, System.currentTimeMillis(), timezone))
                .header("Charset", "UTF-8")
                .method(original.method(), original.body())
                .build()
        return chain.proceed(request)
    }
}

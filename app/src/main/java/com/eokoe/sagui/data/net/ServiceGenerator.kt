package com.eokoe.sagui.data.net

import android.content.Context
import com.eokoe.sagui.BuildConfig
import com.eokoe.sagui.data.net.adapters.BooleanTypeAdapter
import com.eokoe.sagui.data.net.auth.ApiKeyManagerImpl
import com.eokoe.sagui.data.net.auth.AuthenticatorInterceptor
import com.eokoe.sagui.utils.DATE_FORMAT
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Pedro Silva
 */
object ServiceGenerator {

    lateinit var BASE_URL: String
        private set

    lateinit var retrofitBuilder: Retrofit.Builder
        private set

    fun init(context: Context, baseUrl: String) {
        this.BASE_URL = baseUrl

        val gson = GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .excludeFieldsWithoutExposeAnnotation()
//                .registerTypeAdapter(LatLong::class.java, LatLongTypeAdapter.INSTANCE)
                .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
                .create()

        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(loggingInterceptor)
        }

        val apiKeyManager = ApiKeyManagerImpl(context, baseUrl)

        builder.addInterceptor(RequestInterceptor(apiKeyManager))
        builder.addInterceptor(AuthenticatorInterceptor(apiKeyManager))

        retrofitBuilder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    inline fun <reified T> getService(): T = retrofitBuilder.build().create(T::class.java)
}

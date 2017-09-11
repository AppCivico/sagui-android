package com.eokoe.sagui.data.net

import com.eokoe.sagui.BuildConfig
import com.eokoe.sagui.data.entities.LatLong
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

    private lateinit var baseUrl: String

    private lateinit var retrofitBuilder: Retrofit.Builder

    fun init(baseUrl: String) {
        this.baseUrl = baseUrl

        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .registerTypeAdapter(LatLong::class.java, LatLongTypeAdapter.INSTANCE)
                .create()

        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(loggingInterceptor)
        }

        builder.addInterceptor(RequestInterceptor())

        retrofitBuilder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    fun <T> getService(serviceClass: Class<T>): T = retrofitBuilder.build().create(serviceClass)
}

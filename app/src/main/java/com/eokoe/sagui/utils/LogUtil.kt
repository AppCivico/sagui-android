package com.eokoe.sagui.utils

import android.app.Activity
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.eokoe.sagui.BuildConfig
import io.fabric.sdk.android.Fabric

object LogUtil {
    private val tagPrefix = "SaguiApp::"

    private var enableCrashlytics: Boolean = false
    var enableLog: Boolean = BuildConfig.DEBUG

    @JvmStatic
    fun startCrashlytics(context: Activity, enabled: Boolean = !BuildConfig.DEBUG) {
        if (enabled) Fabric.with(context, Crashlytics())
        enableCrashlytics = enabled
    }

    @JvmStatic
    fun debug(tag: Any, msg: String = " ", vararg args: String) {
        println(Log.DEBUG, tag, msg, *args)
    }

    @JvmStatic
    fun info(tag: Any, msg: String = " ", vararg args: String) {
        println(Log.INFO, tag, msg, *args)
    }

    @JvmStatic
    fun error(tag: Any, error: Throwable, msg: String = "", vararg args: String) {
        println(Log.ERROR, tag, "%s(%s) " + msg, error::class.simpleName ?: "Exception", error.message ?: "", *args)
        if (enableCrashlytics) Crashlytics.logException(error)
    }

    private fun println(priority: Int, tag: Any, msg: String, vararg args: String) {
        if (enableLog) {
            val tagStr = tag as? String ?: tag::class.simpleName ?: "LogUtil"
//            Log.println(priority, tagPrefix + tagStr, msg)
            Log.println(priority, tagPrefix + tagStr, String.format(msg, *args))
        }
    }
}
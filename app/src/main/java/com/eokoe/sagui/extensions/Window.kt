package com.eokoe.sagui.extensions

import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.view.Window
import com.eokoe.sagui.R

/**
 * @author Pedro Silva
 * @since 12/09/17
 */

val Window.height: Int
    get() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        return size.y
    }

fun Window.statusBarOverlay() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }*/
        statusBarColor = ColorUtils.blendARGB(statusBarColor, Color.BLACK, 0.53F)
    }
}

fun Window.restoreStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }*/
    }
}
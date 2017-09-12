package com.eokoe.sagui.extensions

import android.graphics.Color
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.view.View
import android.view.Window
import com.eokoe.sagui.R

/**
 * @author Pedro Silva
 * @since 12/09/17
 */
fun Window.statusBarOverlay() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        statusBarColor = ColorUtils.blendARGB(statusBarColor, Color.BLACK, 0.53F)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

fun Window.restoreStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}
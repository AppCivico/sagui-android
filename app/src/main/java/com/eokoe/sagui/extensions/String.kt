package com.eokoe.sagui.extensions

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * @author Pedro Silva
 * @since 11/10/17
 */
@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this)
    }
}
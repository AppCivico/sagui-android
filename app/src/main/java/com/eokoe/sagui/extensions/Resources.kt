package com.eokoe.sagui.extensions

import android.content.res.Resources

/**
 * @author Pedro Silva
 * @since 18/08/17
 */
val Resources.statusBarHeight: Int
    get() {
        var result = 0
        val resourceId = getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = getDimensionPixelSize(resourceId)
        }
        return result
    }
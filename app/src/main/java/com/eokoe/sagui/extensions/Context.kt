package com.eokoe.sagui.extensions

import android.content.Context
import android.content.pm.PackageManager
import com.eokoe.sagui.utils.LogUtil

/**
 * @author Pedro Silva
 * @since 01/09/17
 */
fun Context.getManifestValue(key: String): String? {
    try {
        val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        return ai.metaData.getString(key)
    } catch (e: PackageManager.NameNotFoundException) {
        LogUtil.error(this, e)
    }
    return null
}
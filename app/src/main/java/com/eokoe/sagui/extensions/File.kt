package com.eokoe.sagui.extensions

import android.content.Context
import android.net.Uri
import android.support.v4.content.FileProvider
import com.eokoe.sagui.BuildConfig
import java.io.File

/**
 * @author Pedro Silva
 */
fun File.getUri(context: Context, withAuthority: Boolean = true): Uri =
        if (withAuthority) {
            FileProvider.getUriForFile(context, BuildConfig.FILES_AUTHORITY, this)
        } else {
            Uri.fromFile(this)
        }
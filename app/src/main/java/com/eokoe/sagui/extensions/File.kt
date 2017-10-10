package com.eokoe.sagui.extensions

import android.net.Uri
import java.io.File

/**
 * @author Pedro Silva
 */
fun File.toUri(): Uri? {
    return Uri.fromFile(this)
}
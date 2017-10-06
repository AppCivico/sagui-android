package com.eokoe.sagui.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Pedro Silva
 */
fun Date.format(pattern: String): String? {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}
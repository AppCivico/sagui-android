package com.eokoe.sagui.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Pedro Silva
 */
fun Date.format(pattern: String): String? =
        SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun Date.fromString(pattern: String, date: String): Date? =
        SimpleDateFormat(pattern, Locale.getDefault()).parse(date)
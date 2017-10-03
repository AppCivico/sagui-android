package com.eokoe.sagui.services

import android.content.Context

/**
 * Created by pedroabinajm on 08/08/17.
 */
interface Retry {
    fun schedule(context: Context)
    fun cancel(context: Context)
}

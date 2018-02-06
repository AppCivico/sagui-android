package com.eokoe.sagui.extensions

import android.content.ComponentCallbacks
import org.koin.KoinContext
import org.koin.android.ext.android.releaseContext
import org.koin.standalone.StandAloneContext

/**
 * @author Pedro Silva
 * @since 12/14/17
 */
fun ComponentCallbacks.releaseContext() {
    (StandAloneContext.koinContext as KoinContext).beanRegistry.scopes
            .firstOrNull { it.name == this::class.simpleName }
            ?.run { releaseContext(name) }
}
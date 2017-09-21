package com.eokoe.sagui.widgets.typeface

import android.content.Context
import android.graphics.Typeface

/**
 * Created by pedroabinajm on 15/02/16.
 */
interface CustomTypefaceView {

    fun setTypeface(typeface: Typeface)

    fun getTypeface(): Typeface

    fun getContext(): Context
}

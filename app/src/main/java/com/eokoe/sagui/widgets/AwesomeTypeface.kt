package com.eokoe.sagui.widgets

import android.content.Context
import android.graphics.Typeface
import java.util.*

/**
 * Created by pedroabinajm on 03/02/17.
 */
object AwesomeTypeface {

    enum class Font(private val fileName: String, val value: Int) {
        SANSATION_REGULAR("fonts/awesome.ttf", 1);

        fun asTypeface(context: Context): Typeface {
            var typeface: Typeface? = typefaceCache[this]
            if (typeface == null) {
                typeface = Typeface.createFromAsset(context.assets, fileName)
                typefaceCache.put(this, typeface)
            }

            return typeface!!
        }

        companion object {
            private val typefaceCache = HashMap<Font, Typeface>()
        }
    }

    fun init(view: CustomTypefaceView) {
        view.setTypeface(Font.SANSATION_REGULAR.asTypeface(view.getContext()))
    }
}

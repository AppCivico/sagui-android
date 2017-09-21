package com.eokoe.sagui.widgets

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.eokoe.sagui.widgets.typeface.AwesomeTypeface
import com.eokoe.sagui.widgets.typeface.CustomTypefaceView


/**
 * Created by pedroabinajm on 12/02/16.
 */
class AwesomeTextView : AppCompatTextView, CustomTypefaceView {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        AwesomeTypeface.init(this)
    }
}

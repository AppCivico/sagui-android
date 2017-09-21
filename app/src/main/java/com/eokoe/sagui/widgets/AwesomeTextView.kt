package com.eokoe.sagui.widgets

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.eokoe.sagui.widgets.typeface.AwesomeTypeface
import com.eokoe.sagui.widgets.typeface.CustomTypefaceView


/**
 * @author Pedro Silva
 * @since 20/09/17
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

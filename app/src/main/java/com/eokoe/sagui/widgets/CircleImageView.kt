package com.eokoe.sagui.widgets

import android.content.Context
import android.util.AttributeSet
import de.hdodenhof.circleimageview.CircleImageView


/**
 * @author Pedro Silva
 * @since 25/08/17
 */
class CircleImageView : CircleImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredHeight)
    }
}
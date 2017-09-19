package com.eokoe.sagui.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.LinearLayout
import com.eokoe.sagui.R

/**
 * @author Pedro Silva
 * @since 25/08/17
 */
class SquareLinearLayout : LinearLayout {

    private lateinit var adjustDimen: AdjustableDimension

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SquareLinearLayout, defStyleAttr, 0)
        init(attributes)
        attributes.recycle()
    }

    fun init(attrs: TypedArray) {
        val dimension = attrs.getInt(R.styleable.SquareLinearLayout_srl_adjustDimension, AdjustableDimension.HEIGHT.id)
        adjustDimen = AdjustableDimension.byId(dimension)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (adjustDimen == AdjustableDimension.HEIGHT) {
            setMeasuredDimension(measuredWidth, measuredWidth)
        } else {
            setMeasuredDimension(measuredHeight, measuredHeight)
        }
    }

    enum class AdjustableDimension(val id: Int) {
        HEIGHT(0), WIDTH(1);

        companion object {
            fun byId(id: Int): AdjustableDimension {
                return values().firstOrNull { it.id == id }
                        ?: WIDTH
            }
        }
    }
}
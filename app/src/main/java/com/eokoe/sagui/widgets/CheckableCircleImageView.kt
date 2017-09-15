package com.eokoe.sagui.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.utils.UnitUtils

/**
 * This is a simple wrapper for [LinearLayout] that implements the [Checkable]
 * interface by keeping an internal 'checked' state flag.
 *
 *
 * This can be used as the root view for a custom list item layout for
 * [android.widget.AbsListView] elements with a
 * [choiceMode][android.widget.AbsListView.setChoiceMode] set.
 */
class CheckableCircleImageView : CircleImageView, Checkable, View.OnClickListener {

    private var mChecked = false
    private var mRadioGroup: RadioGroup? = null

    private var mEnableText = true
    private var mText: String? = null
    private lateinit var mTextPaint: Paint
    private var mTextColor: Int = Color.rgb(66, 145, 241)
    private var mTextSize: Float = 0.toFloat()

    private var mOnClickListener: OnClickListener? = null
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.CheckableCircleImageView, defStyleAttr, 0)
        init(attributes)
        attributes.recycle()
    }

    fun init(attributes: TypedArray) {
        mTextColor = attributes.getColor(R.styleable.CheckableCircleImageView_cciv_textColor, mTextColor)
        mTextSize = attributes.getDimension(R.styleable.CheckableCircleImageView_cciv_textSize, UnitUtils.sp2px(resources, 18f))

        mTextPaint = TextPaint()
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mEnableText) {
            val textHeight = mTextPaint.descent() + mTextPaint.ascent()
            canvas.drawText(mText, (width - mTextPaint.measureText(mText)) / 2.0f, (width - textHeight) / 2.0f, mTextPaint)
        }
    }

    override fun isChecked() = mChecked

    override fun setChecked(checked: Boolean) {
        if (checked != mChecked) {
            mChecked = checked
            val checkedId = mRadioGroup?.checkedRadioButtonId
            if (checked && checkedId != null && id != -1 && checkedId != id) {
                (mRadioGroup?.findViewById<View>(checkedId) as? Checkable)?.isChecked = false
                mRadioGroup?.check(id)
            }
            mOnCheckedChangeListener?.onCheckedChanged(this, checked)
            refreshDrawableState()
        }
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        super.setOnClickListener(this)
        mRadioGroup = parent as? RadioGroup
    }

    fun setText(text: String) {
        mText = text
    }

    fun setEnableText(enable: Boolean) {
        mEnableText = enable
    }

    override fun setOnClickListener(l: OnClickListener?) {
        this.mOnClickListener = l
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

    override fun onClick(v: View?) {
        isChecked = true
        mOnClickListener?.onClick(v)
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: CheckableCircleImageView, checked: Boolean)
    }
}

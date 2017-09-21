package com.eokoe.sagui.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.facebook.drawee.view.SimpleDraweeView

/**
 * This is a simple wrapper for [LinearLayout] that implements the [Checkable]
 * interface by keeping an internal 'checked' state flag.
 *
 *
 * This can be used as the root view for a custom list item layout for
 * [android.widget.AbsListView] elements with a
 * [choiceMode][android.widget.AbsListView.setChoiceMode] set.
 */
class CheckableImageView : SimpleDraweeView, Checkable, View.OnClickListener {

    private var mChecked = false
    private var mRadioGroup: RadioGroup? = null

    private var mOnClickListener: OnClickListener? = null
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun isChecked() = mChecked

    override fun setChecked(checked: Boolean) {
        if (checked != mChecked) {
            mChecked = checked
            val checkedId = mRadioGroup?.checkedRadioButtonId
            if (checked) {
                mRadioGroup?.check(id)
                if (checkedId != null && checkedId != -1 && id != -1 && checkedId != id) {
                    (mRadioGroup?.findViewById<View>(checkedId) as? Checkable)?.isChecked = false
                }
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
        var parent = this.parent
        mRadioGroup = null
        while (parent is View && mRadioGroup == null) {
            mRadioGroup = parent as? RadioGroup
            parent = (parent as View).parent
        }
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
        fun onCheckedChanged(view: CheckableImageView, checked: Boolean)
    }
}

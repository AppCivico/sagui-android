package com.eokoe.sagui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.LinearLayout

/**
 * This is a simple wrapper for [android.widget.LinearLayout] that implements the [android.widget.Checkable]
 * interface by keeping an internal 'checked' state flag.
 *
 *
 * This can be used as the root view for a custom list item layout for
 * [android.widget.AbsListView] elements with a
 * [choiceMode][android.widget.AbsListView.setChoiceMode] set.
 */
class CheckableLinearLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), Checkable {

    private var mChecked = false

    override fun isChecked() = mChecked

    override fun setChecked(b: Boolean) {
        if (b != mChecked) {
            mChecked = b
            refreshDrawableState()
            setChildrenChecked(b, this)
        }
    }

    private fun setChildrenChecked(b: Boolean, parent: ViewGroup) {
        (0 until parent.childCount)
                .map { parent.getChildAt(it) }
                .forEach {
                    if (it is Checkable) {
                        it.isChecked = b
                    } else if (it is ViewGroup) {
                        setChildrenChecked(b, it)
                    }
                }
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}

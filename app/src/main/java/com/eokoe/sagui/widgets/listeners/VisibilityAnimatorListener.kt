package com.eokoe.sagui.widgets.listeners

import android.animation.Animator
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View

/**
 * Show the view on the end of animation
 * Created by pedro on 04/08/14.
 */
@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
open class VisibilityAnimatorListener
public constructor(private val view: View, private val visibilityState: Int)
    : Animator.AnimatorListener {

    override fun onAnimationStart(animation: Animator) {
        if (visibilityState == View.VISIBLE) {
            view.visibility = visibilityState
        }
    }

    override fun onAnimationEnd(animation: Animator) {
        if (visibilityState != View.VISIBLE) {
            view.visibility = visibilityState
        }
        view.animate().setListener(null)
    }

    override fun onAnimationCancel(animation: Animator) {}

    override fun onAnimationRepeat(animation: Animator) {}

    companion object {
        fun show(view: View) = VisibilityAnimatorListener(view, View.VISIBLE)

        fun hide(view: View) = VisibilityAnimatorListener(view, View.GONE)

        fun invisible(view: View) = VisibilityAnimatorListener(view, View.INVISIBLE)
    }

}

package com.eokoe.sagui.extensions

import android.animation.Animator
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import com.eokoe.sagui.widgets.listeners.VisibilityAnimatorListener

/**
 * @author Pedro Silva
 * @since 18/08/17
 */
fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

fun View.showAnimated() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        showAnimated(VisibilityAnimatorListener.show(this))
    } else {
        show()
    }
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.showAnimated(listener: Animator.AnimatorListener) {
    changeVisibilityAnimated(fromAlpha = 0f, toAlpha = 1f, listener = listener)
}

fun View.showSlidingTop() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        showSlidingTop(VisibilityAnimatorListener.show(this))
    } else {
        show()
    }
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.showSlidingTop(listener: Animator.AnimatorListener) {
    var parent = this.parent as? View
    if (parent == null) {
        parent = this
    }
    parent.post {
        changeVisibilitySliding(fromAlpha = 0f, toAlpha = 1f,
                fromY = (parent!!.height - top).toFloat(), toY = 0f, listener = listener)
    }
}

fun View.hideAnimated() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        hideAnimated(VisibilityAnimatorListener.hide(this))
    } else {
        hide()
    }
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.hideAnimated(listener: Animator.AnimatorListener) {
    changeVisibilityAnimated(toAlpha = 0f, listener = listener)
}

fun View.hideSlidingBottom() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        hideSlidingBottom(VisibilityAnimatorListener.hide(this))
    } else {
        hide()
    }
}

fun View.invisibleSlidingBottom() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        hideSlidingBottom(VisibilityAnimatorListener.invisible(this))
    } else {
        hide()
    }
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.hideSlidingBottom(listener: Animator.AnimatorListener) {
    post {
        changeVisibilitySliding(fromAlpha = 1f, toAlpha = 0f,
                fromY = 0f, toY = height.toFloat(), listener = listener)
    }
}

fun View.expand() {
    post {
        measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = measuredHeight

        layoutParams.height = 1
        visibility = View.VISIBLE

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                requestLayout()
            }

            override fun willChangeBounds() = true
        }
        animation.duration = targetHeight / context.resources.displayMetrics.density.toLong()
        startAnimation(animation)
    }
}

fun View.collapse() {
    post {
        val initialHeight = measuredHeight

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    visibility = View.GONE
                } else {
                    layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    requestLayout()
                }
            }

            override fun willChangeBounds() = true
        }

        animation.duration = initialHeight / context.resources.displayMetrics.density.toLong()
        startAnimation(animation)
    }
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.changeVisibilityAnimated(
        duration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong(),
        fromAlpha: Float? = null, toAlpha: Float, listener: Animator.AnimatorListener) {
    if (fromAlpha != null) {
        alpha = fromAlpha
    }
    animate().alpha(toAlpha)
            .translationY(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(listener)
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.changeVisibilitySliding(
        duration: Long = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong(),
        fromAlpha: Float? = null, toAlpha: Float, fromY: Float, toY: Float,
        listener: Animator.AnimatorListener) {
    if (fromAlpha != null) {
        alpha = fromAlpha
    }

    translationY = fromY
    animate().alpha(toAlpha)
            .translationY(toY)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .setListener(listener)
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}


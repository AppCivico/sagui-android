package com.eokoe.sagui.extensions

import android.animation.Animator
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
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

fun View.showAnimated() {
    showAnimated(VisibilityAnimatorListener.show(this))
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.showAnimated(listener: Animator.AnimatorListener) {
    changeVisibilityAnimated(fromAlpha = 0f, toAlpha = 1f, listener = listener)
}

fun View.showSlidingTop() {
    showSlidingTop(VisibilityAnimatorListener.show(this))
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.showSlidingTop(listener: Animator.AnimatorListener) {
    val parent = this.parent as? View ?: this
    parent.post {
        changeVisibilitySliding(fromAlpha = 0f, toAlpha = 1f,
                fromY = (parent.height - top).toFloat(), toY = 0f, listener = listener)
    }
}

fun View.hideAnimated() {
    hideAnimated(VisibilityAnimatorListener.hide(this))
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.hideAnimated(listener: Animator.AnimatorListener) {
    changeVisibilityAnimated(toAlpha = 0f, listener = listener)
}

fun View.hideSlidingBottom() {
    hideSlidingBottom(VisibilityAnimatorListener.hide(this))
}

fun View.invisibleSlidingBottom() {
    hideSlidingBottom(VisibilityAnimatorListener.invisible(this))
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.hideSlidingBottom(listener: Animator.AnimatorListener) {
    measure()
    changeVisibilitySliding(fromAlpha = 1f, toAlpha = 0f,
            fromY = 0f, toY = measuredHeight.toFloat(), listener = listener)
}

fun View.measure() {
    val widthSpec = if (layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
        View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY)
    } else {
        layoutParams.width
    }
    val heightSpec = if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
        View.MeasureSpec.makeMeasureSpec((parent as View).height, View.MeasureSpec.EXACTLY)
    } else {
        layoutParams.height
    }
    measure(widthSpec, heightSpec)
}

fun View.expand() {
    measure()
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
    animation.duration = ((targetHeight / context.resources.displayMetrics.density.toLong()) * 1f).toLong()
    startAnimation(animation)
}

fun View.collapse() {
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

    animation.duration = ((initialHeight / context.resources.displayMetrics.density.toLong()) * 1f).toLong()
    startAnimation(animation)
}

@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
fun View.changeVisibilityAnimated(
        duration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong(),
        fromAlpha: Float? = null, toAlpha: Float, listener: Animator.AnimatorListener) {
    alpha = fromAlpha ?: alpha
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
    alpha = fromAlpha ?: alpha
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

fun View.rotate(fromDegrees: Float, toDegrees: Float, duration: Long = 300) {
    val animSet = AnimationSet(true)
    animSet.interpolator = DecelerateInterpolator()
    animSet.fillAfter = true
    animSet.isFillEnabled = true

    val animRotate = RotateAnimation(fromDegrees, toDegrees,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f)

    animRotate.duration = duration
    animRotate.fillAfter = true
    animSet.addAnimation(animRotate)

    startAnimation(animSet)
}

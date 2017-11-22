package com.eokoe.sagui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.eokoe.sagui.utils.UnitUtils
import com.facebook.drawee.drawable.ProgressBarDrawable

/**
 * @author Pedro Silva
 * @since 21/11/17
 */
class CircleProgressBarDrawable(
        context: Context,
        private val barColor: Int = 0,
        strokeWidth: Float = 6f,
        radius: Float = 24f
) : ProgressBarDrawable() {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mLevel = 0
    private val maxLevel = 10000

    private val strokeWidthPx: Float = UnitUtils.dp2px(context, strokeWidth)
    private val radiusPx: Float = UnitUtils.dp2px(context, radius)

    override fun onLevelChange(level: Int): Boolean {
        mLevel = level
        invalidateSelf()
        return true
    }

    override fun draw(canvas: Canvas) {
        if (hideWhenZero && mLevel == 0) {
            return
        }
        drawBar(canvas, maxLevel, backgroundColor)
        drawBar(canvas, mLevel, barColor)
    }

    private fun drawBar(canvas: Canvas, level: Int, color: Int) {
        val bounds = bounds
        val rectF = RectF(bounds.centerX() + radiusPx, bounds.centerY() - radiusPx,
                bounds.centerX() - radiusPx, bounds.centerY() + radiusPx)
        mPaint.color = color
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = strokeWidthPx
        if (level != 0)
            canvas.drawArc(rectF, 0f, (level * 360 / maxLevel).toFloat(),
                    false, mPaint)
    }
}

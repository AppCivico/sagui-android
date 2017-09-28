package com.eokoe.sagui.utils

import android.content.Context
import android.graphics.*
import android.support.annotation.ColorInt
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


/**
 * @author Pedro Silva
 * @since 27/09/17
 */
class BitmapMarker private constructor(val bitmap: Bitmap) {
    val anchorPoints = arrayOf(0.5f, 0.75f)
    val icon: BitmapDescriptor
        get() = BitmapDescriptorFactory.fromBitmap(bitmap)

    companion object {
        inline fun build(context: Context, block: Builder.() -> Unit) = Builder(context).apply(block).build()
    }

    class Builder(val context: Context) {
        // region fields configuration
        @ColorInt
        var color: Int = Color.parseColor("#F41922")
        var text: String? = null
        @ColorInt
        var textColor: Int = Color.BLACK
        var textSizeDP: Int = 14
        var heightDP: Int = 40
        var widthDP: Int = 40
        var arrowHeightDP: Float? = null
            get() = field ?: heightDP / 6f
        var radiusDP: Float? = null
            get() = field ?: Math.min((heightDP - arrowHeightDP!!) / 3f, widthDP / 3f)
        @ColorInt
        var shadowColor: Int = Color.parseColor("#BB000000")
        // endregion

        private val colorPaint = Paint()
            get() {
                field.color = color
                field.isAntiAlias = true
                return field
            }
        private val arrowPaint = Paint()
            get() {
                field.style = Paint.Style.FILL_AND_STROKE
                field.color = color
                field.isAntiAlias = true
                return field
            }
        private val textPaint = Paint()
            get() {
                field.color = textColor
                field.strokeWidth = 1f
                field.isAntiAlias = true
                field.style = Paint.Style.FILL_AND_STROKE
                return field
            }
        private val shadowPaint = Paint()
            get() {
                field.color = shadowColor
                field.isAntiAlias = true
                field.style = Paint.Style.FILL_AND_STROKE
                return field
            }
        private val textBounds = Rect()
        private val path = Path()
        private var markerHeight = 0f
        private var markerWidth = 0f
        private var height = 0
        private var width = 0
        private var radius = 0f
        private var arrowHeight = 0f

        fun build(): BitmapMarker {
            height = UnitUtils.dp2px(context, heightDP.toFloat()).toInt()
            width = UnitUtils.dp2px(context, widthDP.toFloat()).toInt()
            arrowHeight = UnitUtils.dp2px(context, arrowHeightDP!!)
            markerHeight = height - arrowHeight
            markerWidth = width.toFloat()
            radius = UnitUtils.dp2px(context, radiusDP!!)

            val bitmap = Bitmap.createBitmap(width, height + (width / 3), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawARGB(0, 0, 0, 0)
            drawShadow(canvas)
            drawSquare(canvas)
            drawText(canvas)
            drawArrow(canvas)
            return BitmapMarker(bitmap)
        }

        private fun drawShadow(canvas: Canvas) {
            val shadowRadius = width / 2f
            val shadowCY = height - arrowHeight
            val shadowCX = width / 2f
            shadowPaint.maskFilter = BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.INNER)
            shadowPaint.shader = RadialGradient(shadowCX, shadowCY, shadowRadius, shadowColor, Color.TRANSPARENT, Shader.TileMode.MIRROR)
            canvas.drawCircle(shadowCX, shadowCY, shadowRadius, shadowPaint)
        }

        private fun drawText(canvas: Canvas) {
            if (text == null) return
            val textSize = UnitUtils.dp2px(context, textSizeDP.toFloat())
            textPaint.textSize = textSize
            textPaint.getTextBounds(text, 0, text!!.length, textBounds)
            while (textBounds.width() > width) {
                textPaint.textSize = textPaint.textSize - 1f
                textPaint.getTextBounds(text, 0, text!!.length, textBounds)
            }
            canvas.drawText(text, ((markerWidth - textBounds.width()) / 2f) - textBounds.left,
                    (markerHeight + textBounds.height()) / 2f, textPaint)
        }

        private fun drawSquare(canvas: Canvas) {
            val rect = RectF(0f, 0f, markerWidth, markerHeight)
            canvas.drawRoundRect(rect, radius, radius, colorPaint)
        }

        private fun drawArrow(canvas: Canvas) {
            path.fillType = Path.FillType.EVEN_ODD
            val marginArrow = width / 3f
            path.moveTo(marginArrow, markerHeight)
            path.lineTo(width / 2f, height.toFloat())
            path.lineTo(width - marginArrow, markerHeight)
            path.close()
            canvas.drawPath(path, arrowPaint)
        }
    }
}
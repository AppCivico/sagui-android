package com.eokoe.sagui.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.eokoe.sagui.R
import com.eokoe.sagui.utils.UnitUtils



/**
 * @author Pedro Silva
 * @since 17/08/17
 */
@Suppress("JoinDeclarationAndAssignment")
class CircleProgressView : View {

    private lateinit var finishedPaint: Paint
    private lateinit var unfinishedPaint: Paint
    private lateinit var innerCirclePaint: Paint

    private lateinit var textPaint: Paint
    private lateinit var innerBottomTextPaint: Paint

    private val finishedOuterRect = RectF()
    private val unfinishedOuterRect = RectF()

    var attributeResourceId = 0
    var isShowText: Boolean = false
    private var textSize: Float = 0.toFloat()
    private var textColor: Int = 0
    private var innerBottomTextColor: Int = 0
    var progress = 0f
        set(progress) {
            field = progress
            if (progress > max) {
                field %= max.toFloat()
            }
            invalidate()
        }
    var max: Int = 0
        set(max) {
            if (max > 0) {
                field = max
                invalidate()
            }
        }

    private val progressAngle: Float
        get() = progress / max.toFloat() * 360f

    private var finishedStrokeColor: IntArray = IntArray(2)
    private var unfinishedStrokeColor = 0
    private var startingDegree = 0
    private var finishedStrokeWidth = 0f
    private var unfinishedStrokeWidth = 0f
    private var innerBackgroundColor = 0
    private var prefixText: String? = ""
    private var suffixText: String? = "%"
    private var text: String? = null
    private var innerBottomTextSize = 0f
    private var innerBottomText: String? = null
    private var innerBottomTextHeight = 0f

    private val DEFAULT_STROKE_WIDTH: Float
    private val DEFAULT_FINISHED_COLOR = Color.rgb(66, 145, 241)
    private val DEFAULT_UNFINISHED_COLOR = Color.rgb(204, 204, 204)
    private val DEFAULT_TEXT_COLOR = Color.rgb(66, 145, 241)
    private val DEFAULT_INNER_BOTTOM_TEXT_COLOR = Color.rgb(66, 145, 241)
    private val DEFAULT_INNER_BACKGROUND_COLOR = Color.TRANSPARENT
    private val DEFAULT_MAX = 100
    private val DEFAULT_STARTING_DEGREE = 0
    private val DEFAULT_TEXT_SIZE: Float
    private val DEFAULT_INNER_BOTTOM_TEXT_SIZE: Float
    private val MIN_SIZE: Int

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        DEFAULT_TEXT_SIZE = UnitUtils.sp2px(resources, 18f)
        DEFAULT_STROKE_WIDTH = UnitUtils.dp2px(resources, 10f)
        DEFAULT_INNER_BOTTOM_TEXT_SIZE = UnitUtils.sp2px(resources, 18f)
        MIN_SIZE = UnitUtils.dp2px(resources, 100f).toInt()

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyleAttr, 0)
        initByAttributes(attributes)
        attributes.recycle()

        initPainters()
    }

    private fun initPainters() {
        if (isShowText) {
            textPaint = TextPaint()
            textPaint.color = textColor
            textPaint.textSize = textSize
            textPaint.isAntiAlias = true

            innerBottomTextPaint = TextPaint()
            innerBottomTextPaint.color = innerBottomTextColor
            innerBottomTextPaint.textSize = innerBottomTextSize
            innerBottomTextPaint.isAntiAlias = true
        }

        finishedPaint = Paint()
        finishedPaint.shader = SweepGradient(0f, 0f, finishedStrokeColor, arrayOf(0f, progress).toFloatArray())
        finishedPaint.style = Paint.Style.STROKE
        finishedPaint.isAntiAlias = true
        finishedPaint.strokeWidth = finishedStrokeWidth

        unfinishedPaint = Paint()
        unfinishedPaint.color = unfinishedStrokeColor
        unfinishedPaint.style = Paint.Style.STROKE
        unfinishedPaint.isAntiAlias = true
        unfinishedPaint.strokeWidth = unfinishedStrokeWidth

        innerCirclePaint = Paint()
        innerCirclePaint.color = innerBackgroundColor
        innerCirclePaint.isAntiAlias = true
    }

    private fun initByAttributes(attributes: TypedArray) {
        finishedStrokeColor[0] = attributes.getColor(R.styleable.CircleProgressView_cpv_finished_color, DEFAULT_FINISHED_COLOR)
        finishedStrokeColor[1] = attributes.getColor(R.styleable.CircleProgressView_cpv_finished_end_color, finishedStrokeColor[0])
        unfinishedStrokeColor = attributes.getColor(R.styleable.CircleProgressView_cpv_unfinished_color, DEFAULT_UNFINISHED_COLOR)
        isShowText = attributes.getBoolean(R.styleable.CircleProgressView_cpv_show_text, true)
        attributeResourceId = attributes.getResourceId(R.styleable.CircleProgressView_cpv_inner_drawable, 0)

        max = attributes.getInt(R.styleable.CircleProgressView_cpv_max, DEFAULT_MAX)
        progress = attributes.getFloat(R.styleable.CircleProgressView_cpv_progress, 0f)
        finishedStrokeWidth = attributes.getDimension(R.styleable.CircleProgressView_cpv_finished_stroke_width, DEFAULT_STROKE_WIDTH)
        unfinishedStrokeWidth = attributes.getDimension(R.styleable.CircleProgressView_cpv_unfinished_stroke_width, finishedStrokeWidth)

        if (isShowText) {
            if (attributes.getString(R.styleable.CircleProgressView_cpv_prefix_text) != null) {
                prefixText = attributes.getString(R.styleable.CircleProgressView_cpv_prefix_text)
            }
            if (attributes.getString(R.styleable.CircleProgressView_cpv_suffix_text) != null) {
                suffixText = attributes.getString(R.styleable.CircleProgressView_cpv_suffix_text)
            }
            if (attributes.getString(R.styleable.CircleProgressView_cpv_text) != null) {
                text = attributes.getString(R.styleable.CircleProgressView_cpv_text)
            }

            textColor = attributes.getColor(R.styleable.CircleProgressView_cpv_text_color, DEFAULT_TEXT_COLOR)
            textSize = attributes.getDimension(R.styleable.CircleProgressView_cpv_text_size, DEFAULT_TEXT_SIZE)
            innerBottomTextSize = attributes.getDimension(R.styleable.CircleProgressView_cpv_inner_bottom_text_size, DEFAULT_INNER_BOTTOM_TEXT_SIZE)
            innerBottomTextColor = attributes.getColor(R.styleable.CircleProgressView_cpv_inner_bottom_text_color, DEFAULT_INNER_BOTTOM_TEXT_COLOR)
            innerBottomText = attributes.getString(R.styleable.CircleProgressView_cpv_inner_bottom_text)
        }

        innerBottomTextSize = attributes.getDimension(R.styleable.CircleProgressView_cpv_inner_bottom_text_size, DEFAULT_INNER_BOTTOM_TEXT_SIZE)
        innerBottomTextColor = attributes.getColor(R.styleable.CircleProgressView_cpv_inner_bottom_text_color, DEFAULT_INNER_BOTTOM_TEXT_COLOR)
        innerBottomText = attributes.getString(R.styleable.CircleProgressView_cpv_inner_bottom_text)

        startingDegree = attributes.getInt(R.styleable.CircleProgressView_cpv_circle_starting_degree, DEFAULT_STARTING_DEGREE)
        innerBackgroundColor = attributes.getColor(R.styleable.CircleProgressView_cpv_background_color, DEFAULT_INNER_BACKGROUND_COLOR)
    }

    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    fun getFinishedStrokeWidth() = finishedStrokeWidth

    fun setFinishedStrokeWidth(finishedStrokeWidth: Float) {
        this.finishedStrokeWidth = finishedStrokeWidth
        invalidate()
    }

    fun getUnfinishedStrokeWidth() = unfinishedStrokeWidth

    fun setUnfinishedStrokeWidth(unfinishedStrokeWidth: Float) {
        this.unfinishedStrokeWidth = unfinishedStrokeWidth
        invalidate()
    }

    fun getTextSize() = textSize

    fun setTextSize(textSize: Float) {
        this.textSize = textSize
        invalidate()
    }

    fun getTextColor() = textColor

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        invalidate()
    }

    fun getFinishedStrokeColor() = finishedStrokeColor

    fun setFinishedStrokeColor(startColor: Int, endColor: Int) {
        this.finishedStrokeColor[0] = startColor
        this.finishedStrokeColor[1] = endColor
        invalidate()
    }

    fun getUnfinishedStrokeColor() = unfinishedStrokeColor

    fun setUnfinishedStrokeColor(unfinishedStrokeColor: Int) {
        this.unfinishedStrokeColor = unfinishedStrokeColor
        invalidate()
    }

    fun getText() = text

    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    fun getSuffixText() = suffixText

    fun setSuffixText(suffixText: String) {
        this.suffixText = suffixText
        invalidate()
    }

    fun getPrefixText() = prefixText

    fun setPrefixText(prefixText: String) {
        this.prefixText = prefixText
        invalidate()
    }

    fun getInnerBackgroundColor() = innerBackgroundColor

    fun setInnerBackgroundColor(innerBackgroundColor: Int) {
        this.innerBackgroundColor = innerBackgroundColor
        invalidate()
    }


    fun getInnerBottomText() = innerBottomText

    fun setInnerBottomText(innerBottomText: String) {
        this.innerBottomText = innerBottomText
        invalidate()
    }


    fun getInnerBottomTextSize() = innerBottomTextSize

    fun setInnerBottomTextSize(innerBottomTextSize: Float) {
        this.innerBottomTextSize = innerBottomTextSize
        invalidate()
    }

    fun getInnerBottomTextColor() = innerBottomTextColor

    fun setInnerBottomTextColor(innerBottomTextColor: Int) {
        this.innerBottomTextColor = innerBottomTextColor
        invalidate()
    }

    fun getStartingDegree() = startingDegree

    fun setStartingDegree(startingDegree: Int) {
        this.startingDegree = startingDegree
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec))

        //TODO calculate inner circle height and then position bottom text at the bottom (3/4)
        innerBottomTextHeight = (height - height * 3 / 4).toFloat()
    }

    private fun measure(measureSpec: Int): Int {
        var result: Int
        val mode = View.MeasureSpec.getMode(measureSpec)
        val size = View.MeasureSpec.getSize(measureSpec)
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = MIN_SIZE
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth)
        finishedOuterRect.set(delta, delta, width - delta, height - delta)
        unfinishedOuterRect.set(delta, delta, width - delta, height - delta)

        val innerCircleRadius = (width - Math.min(finishedStrokeWidth, unfinishedStrokeWidth) + Math.abs(finishedStrokeWidth - unfinishedStrokeWidth)) / 2f
        canvas.drawCircle(width / 2.0f, height / 2.0f, innerCircleRadius, innerCirclePaint)
        canvas.drawArc(finishedOuterRect, startingDegree.toFloat(), progressAngle, false, finishedPaint)
        canvas.drawArc(unfinishedOuterRect, startingDegree + progressAngle, 360 - progressAngle, false, unfinishedPaint)

        if (isShowText) {
            val text = progress.toInt().toString() + "/" + max
            //if (this.text != null) this.text else prefixText + this.progress + suffixText
            val textHeight = textPaint.descent() + textPaint.ascent()
            canvas.drawText(text, (width - textPaint.measureText(text)) / 2.0f, (width - textHeight) / 2.0f, textPaint)

            if (!TextUtils.isEmpty(getInnerBottomText())) {
                innerBottomTextPaint.textSize = innerBottomTextSize
                val bottomTextBaseline = height.toFloat() - innerBottomTextHeight - (textPaint.descent() + textPaint.ascent()) / 2
                canvas.drawText(getInnerBottomText()!!, (width - innerBottomTextPaint.measureText(getInnerBottomText())) / 2.0f, bottomTextBaseline, innerBottomTextPaint)
            }
        }

        if (attributeResourceId != 0) {
            val bitmap = BitmapFactory.decodeResource(resources, attributeResourceId)
            canvas.drawBitmap(bitmap, (width - bitmap.width) / 2.0f, (height - bitmap.height) / 2.0f, null)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor())
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize())
        bundle.putFloat(INSTANCE_INNER_BOTTOM_TEXT_SIZE, getInnerBottomTextSize())
        bundle.putFloat(INSTANCE_INNER_BOTTOM_TEXT_COLOR, getInnerBottomTextColor().toFloat())
        bundle.putString(INSTANCE_INNER_BOTTOM_TEXT, getInnerBottomText())
        bundle.putInt(INSTANCE_INNER_BOTTOM_TEXT_COLOR, getInnerBottomTextColor())
        bundle.putIntArray(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor())
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor())
        bundle.putInt(INSTANCE_MAX, max)
        bundle.putInt(INSTANCE_STARTING_DEGREE, getStartingDegree())
        bundle.putFloat(INSTANCE_PROGRESS, progress)
        bundle.putString(INSTANCE_SUFFIX, getSuffixText())
        bundle.putString(INSTANCE_PREFIX, getPrefixText())
        bundle.putString(INSTANCE_TEXT, getText())
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth())
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth())
        bundle.putInt(INSTANCE_BACKGROUND_COLOR, getInnerBackgroundColor())
        bundle.putInt(INSTANCE_INNER_DRAWABLE, attributeResourceId)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            textColor = state.getInt(INSTANCE_TEXT_COLOR)
            textSize = state.getFloat(INSTANCE_TEXT_SIZE)
            innerBottomTextSize = state.getFloat(INSTANCE_INNER_BOTTOM_TEXT_SIZE)
            innerBottomText = state.getString(INSTANCE_INNER_BOTTOM_TEXT)
            innerBottomTextColor = state.getInt(INSTANCE_INNER_BOTTOM_TEXT_COLOR)
            finishedStrokeColor = state.getIntArray(INSTANCE_FINISHED_STROKE_COLOR)
            unfinishedStrokeColor = state.getInt(INSTANCE_UNFINISHED_STROKE_COLOR)
            finishedStrokeWidth = state.getFloat(INSTANCE_FINISHED_STROKE_WIDTH)
            unfinishedStrokeWidth = state.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH)
            innerBackgroundColor = state.getInt(INSTANCE_BACKGROUND_COLOR)
            attributeResourceId = state.getInt(INSTANCE_INNER_DRAWABLE)
            initPainters()
            max = state.getInt(INSTANCE_MAX)
            setStartingDegree(state.getInt(INSTANCE_STARTING_DEGREE))
            progress = state.getFloat(INSTANCE_PROGRESS)
            prefixText = state.getString(INSTANCE_PREFIX)
            suffixText = state.getString(INSTANCE_SUFFIX)
            text = state.getString(INSTANCE_TEXT)
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    fun setProgress(percent: String) {
        if (!TextUtils.isEmpty(percent)) {
            progress = Integer.parseInt(percent).toFloat()
        }
    }

    companion object {
        private val INSTANCE_STATE = "saved_instance"
        private val INSTANCE_TEXT_COLOR = "text_color"
        private val INSTANCE_TEXT_SIZE = "text_size"
        private val INSTANCE_TEXT = "text"
        private val INSTANCE_INNER_BOTTOM_TEXT_SIZE = "inner_bottom_text_size"
        private val INSTANCE_INNER_BOTTOM_TEXT = "inner_bottom_text"
        private val INSTANCE_INNER_BOTTOM_TEXT_COLOR = "inner_bottom_text_color"
        private val INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color"
        private val INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color"
        private val INSTANCE_MAX = "max"
        private val INSTANCE_PROGRESS = "progress"
        private val INSTANCE_SUFFIX = "suffix"
        private val INSTANCE_PREFIX = "prefix"
        private val INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width"
        private val INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width"
        private val INSTANCE_BACKGROUND_COLOR = "inner_background_color"
        private val INSTANCE_STARTING_DEGREE = "starting_degree"
        private val INSTANCE_INNER_DRAWABLE = "inner_drawable"
    }
}
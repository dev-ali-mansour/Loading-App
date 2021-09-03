package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()
    private val rect = Rect()

    private var mText = ""
    private var mAllCaps = false
    private var mBackgroundColor = R.attr.backgroundColor
    private var mProgress = 0f

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                setText(context.getString(R.string.loading_message))
                setMBackgroundColor(R.color.colorStatusLoading)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 3000
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE

                    addUpdateListener {
                        mProgress = animatedValue as Float
                        invalidate()
                    }
                    start()
                }
                this.isEnabled = false
            }

            ButtonState.Completed -> {
                setText(context.getString(R.string.completed_message))
                setMBackgroundColor(R.color.colorStatusCompleted)
                valueAnimator.cancel()
                mProgress = 0f
                this.isEnabled = true
            }
            else -> {
            }
        }
        invalidate()
    }

    private val bkgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = R.color.colorPrimary.toColor()
    }

    private val inProgressBkgPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = R.color.colorPrimaryDark.toColor()
    }

    private val inProgressArcPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = Color.YELLOW
    }

    private val textPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = Color.WHITE
        textSize = 60.0f
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.LoadingButton, 0, 0
        ).apply {
            try {
                mAllCaps = getBoolean(R.styleable.LoadingButton_textAllCaps, true)
                val text = getString(R.styleable.LoadingButton_text)
                text?.let { mText = if (mAllCaps) it.uppercase(Locale.getDefault()) else it }
                mBackgroundColor = R.styleable.LoadingButton_backgroundColor.toColor()
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val cornerRadius = 25.0f
        val backgroundWidth = measuredWidth.toFloat()
        val backgroundHeight = measuredHeight.toFloat()

        canvas?.drawColor(mBackgroundColor)
        textPaint.getTextBounds(mText, 0, mText.length, rect)
        canvas?.drawRoundRect(
            0f,
            0f,
            backgroundWidth,
            backgroundHeight,
            cornerRadius,
            cornerRadius,
            bkgPaint
        )

        if (buttonState == ButtonState.Loading) {
            var progressValue = mProgress * measuredWidth.toFloat()
            canvas?.drawRoundRect(
                0f,
                0f,
                progressValue,
                backgroundHeight,
                cornerRadius,
                cornerRadius,
                inProgressBkgPaint
            )

            progressValue = mProgress * 360f
            val arcDiameter = cornerRadius * 2
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            canvas?.drawArc(
                paddingStart + arcDiameter,
                paddingTop.toFloat() + arcDiameter,
                arcRectSize,
                arcRectSize,
                0f,
                progressValue,
                true,
                inProgressArcPaint
            )
        }
        val centerX = measuredWidth.toFloat() / 2
        val centerY = measuredHeight.toFloat() / 2 - rect.centerY()

        canvas?.drawText(mText, centerX, centerY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun setText(text: String) {
        this.mText = if (mAllCaps) text.uppercase(Locale.getDefault()) else text
        invalidate()
        requestLayout()
    }

    private fun setMBackgroundColor(id: Int) {
        mBackgroundColor = id.toColor()
        invalidate()
        requestLayout()
    }

    private fun Int.toColor(): Int = ContextCompat.getColor(context, this)

    fun setState(state: ButtonState) {
        buttonState = state
    }
}
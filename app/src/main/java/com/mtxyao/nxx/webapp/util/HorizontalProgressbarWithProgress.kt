package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ProgressBar
import android.util.TypedValue
import android.view.View
import com.mtxyao.nxx.webapp.R

class HorizontalProgressbarWithProgress : ProgressBar {
    private var DEFAULT_TEXT_SIZE: Int = 10 // sp
    private var DEFAULT_TEXT_COLOR: Long = 0xfffc00d1
    private var DEFAULT_COLOR_UNREACH: Long = 0xffd3d6da
    private var DEFAULT_HEIGHT_UNREACH: Int = 2 // dp
    private var DEFAULT_COLOR_REACH: Long = DEFAULT_TEXT_COLOR
    private var DEFAULT_HEIGHT_REACH: Int = 2 // dp
    private var DEFAULT_TEXT_OFFSET: Int = 2 // dp

    private var mTextSize: Int = sp2px(DEFAULT_TEXT_SIZE)
    private var mTextColor: Long = DEFAULT_TEXT_COLOR
    private var mUnReachColor: Long = DEFAULT_COLOR_UNREACH
    private var mUnReachHeight: Int = dp2px(DEFAULT_HEIGHT_UNREACH)
    private var mReachColor: Long = DEFAULT_COLOR_REACH
    private var mReachHeight: Int = dp2px(DEFAULT_HEIGHT_REACH)
    private var mTextOffset: Int = dp2px(DEFAULT_TEXT_OFFSET)

    private val mPaint = Paint()
    private var mRealWidth: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        // 获取自定义属性值
        obtainStyledAttrs(attrs)
    }

    // 获取自定义属性值
    private fun obtainStyledAttrs(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressbarWithProgress)

        mTextSize = ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_text_size, mTextSize.toFloat()).toInt()
        mTextColor = ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_text_color, mTextColor.toInt()).toLong()
        mTextOffset = ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_text_offset, mTextOffset.toFloat()).toInt()
        mUnReachColor = ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_color, mUnReachColor.toInt()).toLong()
        mUnReachHeight = ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_height, mUnReachHeight.toFloat()).toInt()
        mReachColor = ta.getColor(R.styleable.HorizontalProgressbarWithProgress_progress_reach_color, mReachColor.toInt()).toLong()
        mReachHeight = ta.getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_reach_height, mReachHeight.toFloat()).toInt()
        ta.recycle()

        mPaint.textSize = mTextSize.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        val widthVal = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)

        setMeasuredDimension(widthVal, height)

        mRealWidth = measuredWidth - paddingLeft - paddingRight
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        var result: Int
        val mode = View.MeasureSpec.getMode(heightMeasureSpec)
        val size = View.MeasureSpec.getSize(heightMeasureSpec)

        if (mode == View.MeasureSpec.EXACTLY) {
            result = size
        } else {
            val textHeight = (mPaint.descent() - mPaint.ascent()).toInt()
            result = paddingTop + paddingBottom + Math.max(Math.max(mReachHeight, mUnReachHeight), Math.abs(textHeight))
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(paddingLeft.toFloat(), height.toFloat() / 2)

        var noNeedUnReach = false

        // drawReachBar
        val text = String.format("%.2f", java.lang.Float.valueOf(progress.toFloat())!! / (1024 * 1024)) + "M/" + String.format("%.2f", java.lang.Float.valueOf(max.toFloat())!! / (1024 * 1024)) + "M"
        val textWidth = mPaint.measureText(text)
        val radio = progress * 1.0f / max
        var progressX = radio * mRealWidth
        if (progressX + textWidth > mRealWidth) {
            progressX = (mRealWidth - textWidth)
            noNeedUnReach = true
        }
        val endX = progressX - mTextOffset / 2
        if (endX > 0) {
            mPaint.color = mReachColor.toInt()
            mPaint.strokeWidth = mReachHeight.toFloat()
            canvas.drawLine(0f, 0f, endX, 0f, mPaint)
        }

        // drawText
        mPaint.color = mTextColor.toInt()
        val y = (-((mPaint.descent()).plus(mPaint.ascent())) / 2)
        canvas.drawText(text, progressX, y, mPaint)

        // drawUnReachBar
        if (!noNeedUnReach) {
            val start = progressX + mTextOffset / 2 + textWidth
            mPaint.color = mUnReachColor.toInt()
            mPaint.strokeWidth = mUnReachHeight.toFloat()
            canvas.drawLine(start, 0f, mRealWidth.toFloat(), 0f, mPaint)
        }

        canvas.restore()
    }

    private fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal.toFloat(), resources.displayMetrics).toInt()
    }

    private fun sp2px(spVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal.toFloat(), resources.displayMetrics).toInt()
    }
}
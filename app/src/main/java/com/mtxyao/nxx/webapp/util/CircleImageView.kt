package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView

class CircleImageView : ImageView {
    var mPaint: Paint ? = null
    var mRadius: Float ? = null
    var mScale: Float ? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measuredWidth, measuredHeight)
        mRadius = Float.fromBits(size / 2)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        mPaint = Paint()
        val drawable = drawable
        if (drawable != null) {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mScale = (mRadius!! * 2.0f) / Math.min(bitmap.height, bitmap.width)
            val matrix = Matrix()
            matrix.setScale(mScale!!, mScale!!)
            bitmapShader.setLocalMatrix(matrix)
            mPaint!!.shader = bitmapShader
            canvas!!.drawCircle(mRadius!!, mRadius!!, mRadius!!, mPaint)
        } else {
            super.onDraw(canvas)
        }
    }
}
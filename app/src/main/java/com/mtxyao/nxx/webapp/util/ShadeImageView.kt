package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.graphics.Canvas
import android.widget.ImageView

class ShadeImageView : ImageView {
    private var showShade: Boolean = false

    constructor(context: Context) : super(context)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (showShade)
            canvas!!.drawColor(0x663F3F3F)
    }

    fun isShade () : Boolean {
        return showShade
    }

    fun shade (show: Boolean) {
        showShade = show
        invalidate()
    }
}
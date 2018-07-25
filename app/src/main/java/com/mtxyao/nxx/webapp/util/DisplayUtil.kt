package com.mtxyao.nxx.webapp.util

import android.content.Context

object DisplayUtil {
    fun dip2px (context: Context, dpValue: Float) : Int {
        return (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    fun px2dip (context: Context, pxValue: Float) : Int {
        return (pxValue / context.resources.displayMetrics.density + 0.5f).toInt()
    }
}
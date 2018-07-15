package com.mtxyao.nxx.webapp

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class CustomViewPager : ViewPager {
    var noScroll: Boolean ? = false
    var noCutAnimation: Boolean ? = false

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    fun setNoScroll(flag: Boolean) {
        noScroll = flag
    }

    fun setNoCutAnimation(flag: Boolean) {
        noCutAnimation = flag
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return !noScroll!! && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !noScroll!! && super.onInterceptTouchEvent(ev)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, !noCutAnimation!!)
    }
}
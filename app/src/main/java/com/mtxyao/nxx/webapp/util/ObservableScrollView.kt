package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class ObservableScrollView : ScrollView {
    private var scrollViewListener: ScrollViewListener ? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setListener(listener: ScrollViewListener) {
        scrollViewListener = listener
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        if (scrollViewListener != null) {
            scrollViewListener!!.onScrollChanged(this, x, y, oldx, oldy)
        }
    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
    }
}
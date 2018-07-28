package com.mtxyao.nxx.webapp.util

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout

class AndroidBug5497Workaround {

    companion object {
        fun assistActivity(activity: Activity, isFullPage: Boolean) {
            AndroidBug5497Workaround(activity, isFullPage)
        }
    }

    var isFullPage: Boolean = false
    private var mChildOfContent : View ? = null
    var usableHeightPrevious : Int ? = null
    var frameLayoutParams : FrameLayout.LayoutParams ? = null

    constructor(activity: Activity, isFull: Boolean) {
        isFullPage = isFull
        val content : FrameLayout = activity.findViewById<View>(android.R.id.content) as FrameLayout
        mChildOfContent = content.getChildAt(0)
        mChildOfContent!!.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
        frameLayoutParams = mChildOfContent!!.layoutParams as FrameLayout.LayoutParams
    }

    private fun possiblyResizeChildOfContent () {
        val usableHeightNow = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard = mChildOfContent!!.rootView.height
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                frameLayoutParams!!.height = usableHeightSansKeyboard - heightDifference
            } else {
                frameLayoutParams!!.height = usableHeightSansKeyboard
            }
            mChildOfContent!!.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight () : Int {
        val r = Rect()
        mChildOfContent!!.getWindowVisibleDisplayFrame(r)
        return if (isFullPage) {
            r.bottom
        } else {
            r.bottom - r.top
        }
    }
}
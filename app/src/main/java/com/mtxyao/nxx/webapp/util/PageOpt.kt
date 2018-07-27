package com.mtxyao.nxx.webapp.util

class PageOpt {
    /**
     * 是否显示状态栏以及标题栏
     */
    var showTitleBar: Boolean = false
    /**
     * 是否支持下拉刷新
     */
    var canRef: Boolean = false
    /**
     * 页面状态栏颜色风格是否为黑色
     */
    var statusDark: Boolean = false

    fun setShowTitleBar (showTitle: Boolean): PageOpt {
        showTitleBar = showTitle
        return this
    }

    fun setCanRef (ref: Boolean): PageOpt {
        canRef = ref
        return this
    }

    fun setStatusDark (dark: Boolean): PageOpt {
        statusDark = dark
        return this
    }
}
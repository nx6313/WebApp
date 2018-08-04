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
    /**
     * 页面中的图片裁剪功能是否可以调整裁剪框大小
     */
    var freeStyleCropEnabled: Boolean = false
    /**
     * 页面标题栏透明
     */
    var titleBarTransparency: Boolean = false
    /**
     * 页面标题栏内容高亮
     */
    var titleBarHighlight: Boolean = false
    /**
     * 页面webView是否全屏
     */
    var webViewFull: Boolean = false
    /**
     * 页面标题栏以及状态栏颜色
     */
    var titleBarColor: String = ""
    /**
     * 页面所需参数
     */
    var pageParams: MutableMap<String, Any> ? = null

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

    fun setFreeStyleCropEnabled (freeStyleCrop: Boolean): PageOpt {
        freeStyleCropEnabled = freeStyleCrop
        return this
    }

    fun setTitleBarTransparency (transparency: Boolean): PageOpt {
        titleBarTransparency = transparency
        return this
    }

    fun setTitleBarHighlight (highlight: Boolean): PageOpt {
        titleBarHighlight = highlight
        return this
    }

    fun setWebViewFull (full: Boolean): PageOpt {
        webViewFull = full
        return this
    }

    fun setTitleBarColor (color: String): PageOpt {
        titleBarColor = color
        return this
    }

    fun setPageParams (params: MutableMap<String, Any>?): PageOpt {
        pageParams = params
        return this
    }
}
package com.mtxyao.nxx.webapp.util

class PageOpt {
    var showTitleBar: Boolean = false

    fun setShowTitleBar (showTitle: Boolean): PageOpt {
        showTitleBar = showTitle
        return this
    }
}
package com.mtxyao.nxx.webapp

import com.mtxyao.nxx.webapp.util.PageOpt
import kotlinx.android.synthetic.main.activity_app.*

class AppActivity : BaseWebActivity() {

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_app
    }

    override fun createAfter() {
        appTitle.text = intent.getStringExtra("titleName")
    }

    override fun getPageOpt(): PageOpt {
        val pageOpt = super.getPageOpt()
        if (intent.getBooleanExtra("fullPage", false)) {
            pageOpt.setWebViewFull(true)
                    .setTitleBarTransparency(true)
        } else {
            pageOpt.setTitleBarColor(intent.getStringExtra("titleBarColor"))
        }
        if (intent.getBooleanExtra("titleBarHighlight", false)) {
            pageOpt.setStatusDark(false)
                    .setTitleBarHighlight(true)
        }
        return pageOpt
    }

    override fun setPageUrl(): String {
        return intent.getStringExtra("webUri")
    }
}

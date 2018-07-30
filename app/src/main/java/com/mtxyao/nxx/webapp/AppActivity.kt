package com.mtxyao.nxx.webapp

import com.mtxyao.nxx.webapp.util.PageOpt
import com.mtxyao.nxx.webapp.util.Urls
import kotlinx.android.synthetic.main.activity_app.*

class AppActivity : BaseWebActivity() {

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_app
    }

    override fun createAfter() {
        appTitle.text = intent.getStringExtra("titleName")
    }

    override fun getPageOpt(): PageOpt {
        return super.getPageOpt().setStatusDark(false)
                .setTitleBarTransparency(true)
                .setTitleBarHighlight(intent.getBooleanExtra("titleBarHighlight", false))
                .setWebViewFull(true)
    }

    override fun setPageUrl(): String {
        return "${Urls.WEB_BEFORE}#/${intent.getStringExtra("webUri")}"
    }
}

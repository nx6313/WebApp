package com.mtxyao.nxx.webapp

import com.mtxyao.nxx.webapp.util.PageOpt
import com.mtxyao.nxx.webapp.util.Urls

class ClientFollowActivity : BaseWebActivity() {

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_client_follow
    }

    override fun getPageOpt(): PageOpt {
        return super.getPageOpt().setStatusDark(true)
    }

    override fun setPageUrl(): String {
        return "${Urls.WEB_BEFORE}#/app-client-follow"
    }
}

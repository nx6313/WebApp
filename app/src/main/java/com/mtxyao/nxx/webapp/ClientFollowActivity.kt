package com.mtxyao.nxx.webapp

import com.mtxyao.nxx.webapp.util.PageOpt

class ClientFollowActivity : BaseWebActivity() {

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_client_follow
    }

    override fun getPageOpt(): PageOpt {
        return super.getPageOpt().setStatusDark(true)
    }

    override fun setPageUrl(): String {
        return "app-client-follow"
    }
}

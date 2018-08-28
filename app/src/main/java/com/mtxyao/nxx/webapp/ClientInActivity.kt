package com.mtxyao.nxx.webapp

import com.mtxyao.nxx.webapp.util.PageOpt

class ClientInActivity : BaseWebActivity() {

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_client_in
    }

    override fun getPageOpt(): PageOpt {
        return super.getPageOpt().setStatusDark(true).setFreeStyleCropEnabled(true)
    }

    override fun setPageUrl(): String {
        return "app-client-in-task"
    }

    override fun onResume() {
        super.onResume()
        refPage()
    }
}

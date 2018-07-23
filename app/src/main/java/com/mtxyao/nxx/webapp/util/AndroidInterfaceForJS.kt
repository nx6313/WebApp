package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import com.google.gson.Gson
import com.just.agentweb.AgentWeb
import com.mtxyao.nxx.webapp.entity.UserData

class AndroidInterfaceForJS(agentWeb: AgentWeb) {
    private var deliver: Handler = Handler(Looper.getMainLooper())
    private var context: Context = MyApplication.instance!!.applicationContext
    private var mAgentWeb: AgentWeb = agentWeb

    @JavascriptInterface
    open fun callAndroid (msg: String) {
        when (msg) {
            "saveUserInfoForAndroid" -> {
                deliver.post {
                    callByAndroid(msg)
                }
            }
        }
    }

    // 调用web的方法
    private fun callByAndroid (jsFunName: String) {
        when (jsFunName) {
            "saveUserInfoForAndroid" -> {
                val userData: UserData ? = UserDataUtil.getUserData(context)
                mAgentWeb!!.jsAccessEntrace.quickCallJs(jsFunName, Gson().toJson(userData))
            }
        }
    }
}
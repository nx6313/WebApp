package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.util.Log
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.service.JPushMessageReceiver

class MyJPushMessageReceiver : JPushMessageReceiver() {

    override fun onAliasOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onAliasOperatorResult(p0, p1)
        Log.d("onAliasOperatorResult", p1!!.toString())
    }

    override fun onTagOperatorResult(p0: Context?, p1: JPushMessage?) {
        super.onTagOperatorResult(p0, p1)
        Log.d("onTagOperatorResult", p1!!.toString())
    }
}
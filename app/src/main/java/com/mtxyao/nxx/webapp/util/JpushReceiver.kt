package com.mtxyao.nxx.webapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.JPushInterface

class JpushReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("JPushReceive", intent!!.action + " | " + intent.extras)
        when (intent!!.action) {
            JPushInterface.ACTION_MESSAGE_RECEIVED -> {
                val bundle: Bundle = intent.extras
                val title: String = bundle.getString(JPushInterface.EXTRA_TITLE)
                val message: String = bundle.getString(JPushInterface.EXTRA_MESSAGE)
                val extras: String = bundle.getString(JPushInterface.EXTRA_EXTRA)
                val file: String = bundle.getString(JPushInterface.EXTRA_MSG_ID)
            }
        }
    }
}
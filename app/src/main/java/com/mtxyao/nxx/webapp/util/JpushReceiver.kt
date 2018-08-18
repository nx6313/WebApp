package com.mtxyao.nxx.webapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.JPushInterface

class JpushReceiver : BroadcastReceiver() {
    companion object {
        var BCS_ACTION_INDEX_MSG = "com.dc.BCS_ACTION_INDEX_MSG"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("JPushReceive", intent!!.action + " | " + intent.extras)
        when (intent!!.action) {
            JPushInterface.ACTION_MESSAGE_RECEIVED -> {
                val bundle: Bundle = intent.extras
                val message: String = bundle.getString(JPushInterface.EXTRA_MESSAGE)
                Log.d("MESSAGE_RECEIVED ->", message)
                val msgIntent = Intent()
                msgIntent.action = BCS_ACTION_INDEX_MSG
                msgIntent.putExtra("data", message)
                context!!.sendBroadcast(msgIntent)
            }
        }
    }
}
package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.widget.Toast

class AndroidInterfaceForJS {
    private var deliver: Handler = Handler(Looper.getMainLooper())
    private var context: Context = MyApplication.instance!!.applicationContext

    @JavascriptInterface
    open fun callAndroid (msg: String) {
        deliver.post {
            ComFun.showToast(context, "调用android：$msg", Toast.LENGTH_SHORT)
        }
    }
}
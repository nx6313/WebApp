package com.mtxyao.nxx.webapp.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import java.io.File

object ComFun {
    var mToast : Toast ? = null

    /**
     * 显示Toast提示信息
     */
    fun showToast (context: Context, txt: String, duration: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(context, txt, duration)
            mToast!!.setGravity(Gravity.CENTER, 0, 0)
        } else {
            mToast!!.setText(txt)
            mToast!!.duration = duration
        }
        mToast!!.show()
    }

    /**
     * 关闭当前正显示的Toast提示信息
     */
    fun hideToast () {
        if (mToast != null) {
            mToast!!.cancel()
        }
    }

    /**
     * 打开输入法
     */
    fun openIME (context: Context, editText: EditText) {
        val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * 关闭输入法
     */
    fun closeIME (context: Context, view: View) {
        if (view?.windowToken != null) {
            val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) {
                inputMethodManager.hideSoftInputFromInputMethod(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth (context: Context) : Int {
        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    /**
     * 获取程序版本信息
     */
    fun getVersionInfo (context: Context) : PackageInfo {
        val packageManager: PackageManager = context.packageManager
        return packageManager.getPackageInfo(context.packageName, 0)
    }

    /**
     * 安装apk
     */
    fun installApk (context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        var data: Uri ? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, "com.fy.niu.fyreorder.fileprovider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            data = Uri.fromFile(file)
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }
}
package com.mtxyao.nxx.webapp.util

import android.app.Activity
import android.app.AlertDialog
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
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.mtxyao.nxx.webapp.R
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
     * 获取状态栏高度
     */
    fun getStateBarHeight (): Int {
        var stateBarHeight = 0
        val resourceId = MyApplication.instance!!.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            stateBarHeight = MyApplication.instance!!.resources.getDimensionPixelSize(resourceId)
        }
        return stateBarHeight
    }

    private var loadingDialog: AlertDialog ? = null
    /**
     * 显示loading弹窗
     */
    fun showLoading (activity: Activity, loadingTipValue: String, cancelable: Boolean?) {
        loadingDialog = AlertDialog.Builder(activity, R.style.MyDialogStyle).setCancelable(cancelable!!).create()
        loadingDialog!!.show()
        val params: WindowManager.LayoutParams = loadingDialog!!.window.attributes
        params.width = getScreenWidth() * 3 / 4
        loadingDialog!!.window.attributes = params

        val win: Window = loadingDialog!!.window
        val loadingView: View = activity.layoutInflater.inflate(R.layout.loading_dialog, null)
        win.setContentView(loadingView)
        val loadingTip: TextView = loadingView.findViewById(R.id.loadingTip)
        loadingTip.text = loadingTipValue
    }

    /**
     * 隐藏loading弹窗
     */
    fun hideLoading () {
        if (loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
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
        if (view.windowToken != null) {
            val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth () : Int {
        val wm: WindowManager = MyApplication.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    /**
     * 获取程序版本信息
     */
    fun getVersionInfo () : PackageInfo {
        val packageManager: PackageManager = MyApplication.instance!!.packageManager
        return packageManager.getPackageInfo(MyApplication.instance!!.packageName, 0)
    }

    /**
     * 安装apk
     */
    fun installApk (context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        var data: Uri?
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
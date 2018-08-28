package com.mtxyao.nxx.webapp.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import android.widget.TextView
import cn.jpush.android.api.JPushInterface
import com.google.gson.Gson
import com.just.agentweb.AgentWeb
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.LoginActivity
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.SecondActivity
import com.mtxyao.nxx.webapp.entity.UserData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class AndroidInterfaceForJS(fgt: BaseFragment, agentWeb: AgentWeb, statusBar: View?, titleWrap: View?, pageOp: PageOpt?) {
    private var deliver: Handler = Handler(Looper.getMainLooper())
    private var fragment: BaseFragment = fgt
    private var mAgentWeb: AgentWeb = agentWeb
    private var sBar: View ? = statusBar
    private var tWrap: View ? = titleWrap
    private var pageOpt: PageOpt ? = pageOp

    @JavascriptInterface
    fun callAndroid (msg: String, params: String) {
        when (msg) {
            "exitLogin" -> {
                deliver.post {
                    val userData: UserData? = UserDataUtil.getUserData(fragment.context!!)
                    userData!!.needLogin = true
                    UserDataUtil.setUserData(fragment.context!!, userData)
                    fragment.activity!!.finish()
                    JPushInterface.stopPush(fragment.context)
                    val loginIntent = Intent(fragment.context, LoginActivity::class.java)
                    fragment.context!!.startActivity(loginIntent)
                }
            }
            "updateTitleBar" -> {
                deliver.post {
                    val pars = JSONObject(params)
                    if (pars.has("title")) {
                        tWrap!!.findViewById<TextView>(R.id.pageTitle).text = pars["title"] as String
                    }
                    if (pars.has("bg") && pars["bg"] != "") {
                        sBar!!.setBackgroundColor(Color.parseColor(pars["bg"] as String))
                        tWrap!!.setBackgroundColor(Color.parseColor(pars["bg"] as String))
                    } else {
                        var defalutBg = "#007EC8"
                        if (pageOpt!!.titleBarColor != "") {
                            defalutBg = pageOpt!!.titleBarColor
                        }
                        sBar!!.setBackgroundColor(Color.parseColor(defalutBg))
                        tWrap!!.setBackgroundColor(Color.parseColor(defalutBg))
                    }
                    if (pars.has("dos")) {
                        if (pars.getBoolean("dos")) {
                            tWrap!!.findViewById<LinearLayout>(R.id.titleBtnWrap).visibility = View.VISIBLE
                        } else {
                            tWrap!!.findViewById<LinearLayout>(R.id.titleBtnWrap).visibility = View.GONE
                        }
                    }
                }
            }
            "saveUserInfo" -> {
                deliver.post {
                    callByAndroid(msg)
                }
            }
            "selectPic" -> {
                deliver.post {
                    if (ContextCompat.checkSelfPermission(fragment.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(fragment.activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), BaseFragment.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
                    } else {
                        val intent = Intent(Intent.ACTION_GET_CONTENT, null)
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                        fragment.startActivityForResult(intent, BaseFragment.PICKER_PIC)
                    }
                }
            }
            "selectPhoto" -> {
                deliver.post {
                    val outputImage = File(fragment.context!!.externalCacheDir, "output_image.jpg")
                    if (outputImage.exists()) {
                        outputImage.delete()
                    }
                    outputImage.createNewFile()
                    BaseFragment.imageUri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(fragment.context!!, MyApplication.instance!!.applicationContext.packageName + ".provider", outputImage)
                    } else {
                        Uri.fromFile(outputImage)
                    }

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, BaseFragment.imageUri)
                    fragment.startActivityForResult(intent, BaseFragment.PICKER_PHOTO)
                }
            }
            "skipPage" -> {
                val pars = JSONObject(params)
                val webPath = pars["path"] as String
                val title = pars["title"] as String
                val titleDos = (pars["titleDos"] as JSONArray).toString()
                val titleBarColor = pars["titleBarColor"] as String
                val statusBarStyle = pars["statusBarStyle"] as String
                val fullPage = pars["fullPage"] as Boolean
                val pageParams = (pars["pageParams"] as JSONObject).toString()
                val skipIntent = Intent(fragment.context, SecondActivity::class.java)
                skipIntent.putExtra("webUri", webPath)
                skipIntent.putExtra("titleName", title)
                skipIntent.putExtra("pageOpts", SecondActivity.PageOptsSerializable(titleDos, pageParams))
                skipIntent.putExtra("titleBarColor", titleBarColor)
                skipIntent.putExtra("titleBarHighlight", statusBarStyle == "highlight")
                skipIntent.putExtra("fullPage", fullPage)
                fragment.context!!.startActivity(skipIntent)
            }
        }
    }

    // 调用web的方法
    private fun callByAndroid (jsFunName: String) {
        when (jsFunName) {
            "saveUserInfo" -> {
                val userData: UserData ? = UserDataUtil.getUserData(fragment.context!!)
                mAgentWeb.jsAccessEntrace.quickCallJs(jsFunName, Gson().toJson(userData))
            }
        }
    }

    @JavascriptInterface
    fun setTimeOut (event: String, duration: Int) {
        Handler().postDelayed({
            deliver.post {
                mAgentWeb.jsAccessEntrace.quickCallJs("androidEvent", event)
            }
        }, duration.toLong())
    }
}
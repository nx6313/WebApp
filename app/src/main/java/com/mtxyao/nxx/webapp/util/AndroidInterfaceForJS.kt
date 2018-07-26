package com.mtxyao.nxx.webapp.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.TextView
import com.google.gson.Gson
import com.just.agentweb.AgentWeb
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.entity.UserData
import org.json.JSONObject
import java.io.File

class AndroidInterfaceForJS(fgt: BaseFragment, agentWeb: AgentWeb, titleWrap: View?) {
    private var deliver: Handler = Handler(Looper.getMainLooper())
    private var fragment: BaseFragment = fgt
    private var mAgentWeb: AgentWeb = agentWeb
    private var tWrap: View ? = titleWrap

    @JavascriptInterface
    open fun callAndroid (msg: String, params: String) {
        when (msg) {
            "updateTitleBar" -> {
                deliver.post {
                    val pars = JSONObject(params)
                    val title: String = pars["title"] as String
                    tWrap!!.findViewById<TextView>(R.id.pageTitle).text = title
                }
            }
            "saveUserInfoForAndroid" -> {
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
        }
    }

    // 调用web的方法
    private fun callByAndroid (jsFunName: String) {
        when (jsFunName) {
            "saveUserInfoForAndroid" -> {
                val userData: UserData ? = UserDataUtil.getUserData(fragment.context!!)
                mAgentWeb!!.jsAccessEntrace.quickCallJs(jsFunName, Gson().toJson(userData))
            }
        }
    }

    @JavascriptInterface
    open fun setTimeOut (event: String, duration: Int) {
        Handler().postDelayed({
            deliver.post {
                mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", event)
            }
        }, duration.toLong())
    }
}